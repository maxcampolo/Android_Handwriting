package com.maxcampolo.fingerpaint;

import java.util.ArrayList;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.*;
import android.view.MotionEvent;

public class DrawingView extends View {
	
	private Path drawPath;
	private Paint drawPaint;
	private Paint canvasPaint;
	private int paintColor = 0xFF000000;
	private Canvas drawingCanvas;
	private Bitmap canvasBitmap;
	private ArrayList<Float> pathPoints;
	
	public DrawingView (Context context, AttributeSet attributes) {
		super(context, attributes);
		setupDrawing();
	}
	
	private void setupDrawing() {
		//set up drawing area for user drawing
		drawPath = new Path();
		drawPaint = new Paint();
		pathPoints = new ArrayList<Float>();
		
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(10);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		
		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}
	
	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		//when custom drawing view is assigned size - view given size
		super.onSizeChanged(width, height, oldWidth, oldHeight);
		
		canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		drawingCanvas = new Canvas(canvasBitmap);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//Draw the view by overriding onDraw method
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//register user touches when they draw on the drawing view
		float xCoord = event.getX();
		float yCoord = event.getY();
		//boolean touched;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			pathPoints.add(xCoord);
			pathPoints.add(yCoord);
			drawPath.moveTo(xCoord, yCoord);
			break;
		case MotionEvent.ACTION_MOVE:
			pathPoints.add(xCoord);
			pathPoints.add(yCoord);
			drawPath.lineTo(xCoord, yCoord);
			break;
		case MotionEvent.ACTION_UP:
			pathPoints.add((float)255);
			pathPoints.add((float)0);
			drawingCanvas.drawPath(drawPath, drawPaint);
			drawPath.reset();
			break;
		default:
			return false;
		}
		
		invalidate();              //execute onDraw method
		return true;
	}
	
	public void setColor(String newColor) {
		invalidate();
		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
	}
	
	//method to get arraylist of path points for drawing
	public ArrayList<Float> getPathPoints() {
		return pathPoints;
	}
	
	public void startNew() {
		drawingCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		pathPoints = new ArrayList<Float>();
		invalidate();
	}
	
}
