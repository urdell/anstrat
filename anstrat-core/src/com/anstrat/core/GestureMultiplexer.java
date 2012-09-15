package com.anstrat.core;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/** An {@link GestureListener} that delegates to an ordered list of other GestureListeners. Delegation for an event stops if a
 * processor returns true, which indicates that the event was handled.
 * 
 * (Copied from {@link InputMultiplexer} but modified for the GestureListener interface)
 */
public class GestureMultiplexer implements GestureListener {
	private Array<GestureListener> processors = new Array<GestureListener>(4);

	public GestureMultiplexer () {
	}

	public GestureMultiplexer (GestureListener... processors) {
		for (GestureListener processor : processors)
			this.processors.add(processor);
	}

	public void addProcessor(int index, GestureListener processor) {
		processors.insert(index, processor);
	}
	
	public void removeProcessor(int index) {
		processors.removeIndex(index);
	}
	
	public void addProcessor (GestureListener processor) {
		processors.add(processor);
	}

	public void removeProcessor (GestureListener processor) {
		processors.removeValue(processor, true);
	}

	/**
	 * @return the number of processors in this multiplexer
	 */
	public int size() {
		return processors.size;
	}
	
	public void clear () {
		processors.clear();
	}

	public void setProcessors (Array<GestureListener> processors) {
		this.processors = processors;
	}

	public Array<GestureListener> getProcessors () {
		return processors;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		for(GestureListener processor : processors)
			if (processor.touchDown(x, y, pointer, button)) return true;
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		for(GestureListener processor : processors)
			if (processor.tap(x, y, count, button)) return true;
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		for(GestureListener processor : processors)
			if (processor.longPress(x, y)) return true;
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		for(GestureListener processor : processors)
			if (processor.fling(velocityX, velocityY, button)) return true;
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		for(GestureListener processor : processors)
			if (processor.pan(x, y, deltaX, deltaY)) return true;
		return false;
	}

	@Override
	public boolean zoom(float originalDistance, float currentDistance) {
		for(GestureListener processor : processors)
			if (processor.zoom(originalDistance, currentDistance)) return true;
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer) {
		for(GestureListener processor : processors)
			if (processor.pinch(initialFirstPointer, initialSecondPointer, firstPointer, secondPointer)) return true;
		return false;
	}
}
