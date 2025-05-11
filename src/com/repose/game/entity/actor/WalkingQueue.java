package com.repose.game.entity.actor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

import com.repose.game.world.map.Direction;
import com.repose.game.world.map.TilePosition;

/**
 * A queue of {@link Direction}s which a {@link Mob} will follow.
 *
 * @author Graham
 */
public final class WalkingQueue {

	/**
	 * The Mob this WalkingQueue belongs to.
	 */
	private final Actor actor;

	/**
	 * The Deque of active points in this WalkingQueue.
	 */
	private final Deque<TilePosition> points = new ArrayDeque<>();

	/**
	 * The Deque of previous points in this WalkingQueue.
	 */
	private final Deque<TilePosition> previousPoints = new ArrayDeque<>();

	/**
	 * The running status of this WalkingQueue.
	 */
	private boolean running;

	/**
	 * Creates the WalkingQueue.
	 *
	 * @param actor The {@link Mob} the WalkingQueue is for.
	 */
	public WalkingQueue(Actor actor) {
		this.actor = actor;
	}

	/**
	 * Adds a first step into this WalkingQueue.
	 *
	 * @param next The {@link TilePosition} of the step.
	 */
	public void addFirstStep(TilePosition next) {
		this.points.clear();
		this.running = false;

		/*
		 * We need to connect 'current' and 'next' whilst accounting for the fact that
		 * the client and server might be out of sync (i.e. what the client thinks is
		 * 'current' is different to what the server thinks is 'current').
		 *
		 * First try to connect them via points from the previous queue.
		 */
		Queue<TilePosition> backtrack = new ArrayDeque<>();

		while (!this.previousPoints.isEmpty()) {
			TilePosition position = this.previousPoints.pollLast();
			backtrack.add(position);

			if (position.equals(next)) {
				backtrack.forEach(this::addStep);
				this.previousPoints.clear();
				return;
			}
		}

		/* If that doesn't work, connect the points directly. */
		this.previousPoints.clear();
		addStep(next);
	}

	/**
	 * Adds a step to this WalkingQueue.
	 *
	 * @param next The {@link TilePosition} of the step.
	 */
	public void addStep(TilePosition next) {
		TilePosition current = this.points.peekLast();

		/*
		 * If current equals next, addFirstStep doesn't end up adding anything points
		 * queue. This makes peekLast() return null. If it does, the correct behaviour
		 * is to fill it in with mob.getPosition().
		 */
		if (current == null) {
			current = this.actor.getPosition();
		}

		addStep(current, next);
	}

	/**
	 * Clears this WalkingQueue.
	 */
	public void clear() {
		this.points.clear();
		this.running = false;
		this.previousPoints.clear();
	}

	/**
	 * Returns whether or not this WalkingQueue has running enabled.
	 *
	 * @return {@code true} iff this WalkingQueue has running enabled.
	 */
	public boolean isRunning() {
		return this.running;
	}

	/**
	 * Pulses this WalkingQueue.
	 */
	public void pulse() {
		TilePosition position = this.actor.getPosition();
		int height = position.getPlane();

		Direction firstDirection = Direction.NONE;
		Direction secondDirection = Direction.NONE;

		TilePosition next = this.points.poll();
		if (next != null) {
			firstDirection = Direction.between(position, next);

			this.previousPoints.add(next);
			position = new TilePosition(next.getX(), next.getY(), height);
			this.actor.setLastDirection(firstDirection);

			if (this.running) {
				next = this.points.poll();
				if (next != null) {
					secondDirection = Direction.between(position, next);
					this.previousPoints.add(next);
					position = new TilePosition(next.getX(), next.getY(), height);
					this.actor.setLastDirection(secondDirection);
				}
			}
		}
		this.actor.setDirections(firstDirection, secondDirection);
		this.actor.setPosition(position);

	}

	/**
	 * Sets the running flag status of this WalkingQueue.
	 *
	 * @param running The running flag.
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Gets the size of this WalkingQueue, which is the number of points remaining
	 * in it.
	 *
	 * @return The size.
	 */
	public int size() {
		return this.points.size();
	}

	/**
	 * Adds the {@code next} step to this WalkingQueue.
	 *
	 * @param current The current {@link TilePosition}.
	 * @param next    The next Position.
	 */
	private void addStep(TilePosition current, TilePosition next) {
		int nextX = next.getX(), nextY = next.getY(), height = next.getPlane();
		int deltaX = nextX - current.getX();
		int deltaY = nextY - current.getY();

		int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));

		for (int count = 0; count < max; count++) {
			if (deltaX < 0) {
				deltaX++;
			} else if (deltaX > 0) {
				deltaX--;
			}

			if (deltaY < 0) {
				deltaY++;
			} else if (deltaY > 0) {
				deltaY--;
			}

			TilePosition step = new TilePosition(nextX - deltaX, nextY - deltaY, height);

			this.points.add(step);
		}
	}

}