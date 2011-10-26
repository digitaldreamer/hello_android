/***
 * Excerpted from "Hello, Android! 3e",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/

package org.example.wallpaper;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;


import android.service.wallpaper.WallpaperService;

import android.view.SurfaceHolder;



public class Wallpaper extends WallpaperService {
   
   private class MyEngine extends Engine {
      
      // Engine implementation goes here...
      
      
      private GLRenderer glRenderer;
      private GL10 gl;
      private EGL10 egl;
      private EGLContext glc;
      private EGLDisplay glDisplay;
      private EGLSurface glSurface;

      private ExecutorService executor;
      private Runnable drawCommand;
      

      
      
      @Override
      public void onCreate(final SurfaceHolder holder) {
         super.onCreate(holder);
         
         executor = Executors.newSingleThreadExecutor();

         drawCommand = new Runnable() {
            public void run() {
               glRenderer.onDrawFrame(gl);
               egl.eglSwapBuffers(glDisplay, glSurface);
               if (isVisible()
                     && egl.eglGetError() != EGL11.EGL_CONTEXT_LOST) {
                  executor.execute(drawCommand);
               }
            }
         };
         
      }
      

      
      @Override
      public void onDestroy() {
         
         executor.shutdownNow();
         
         super.onDestroy();
      }
      

      
      @Override
      public void onSurfaceCreated(final SurfaceHolder holder) {
         super.onSurfaceCreated(holder);
         
         Runnable surfaceCreatedCommand = new Runnable() {
            @Override
            public void run() {
               // Initialize OpenGL
               egl = (EGL10) EGLContext.getEGL();
               glDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
               int[] version = new int[2];
               egl.eglInitialize(glDisplay, version);
               int[] configSpec = { EGL10.EGL_RED_SIZE, 5,
                     EGL10.EGL_GREEN_SIZE, 6, EGL10.EGL_BLUE_SIZE,
                     5, EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
               EGLConfig[] configs = new EGLConfig[1];
               int[] numConfig = new int[1];
               egl.eglChooseConfig(glDisplay, configSpec, configs,
                     1, numConfig);
               EGLConfig config = configs[0];

               glc = egl.eglCreateContext(glDisplay, config,
                     EGL10.EGL_NO_CONTEXT, null);

               glSurface = egl.eglCreateWindowSurface(glDisplay,
                     config, holder, null);
               egl.eglMakeCurrent(glDisplay, glSurface, glSurface,
                     glc);
               gl = (GL10) (glc.getGL());

               // Initialize Renderer
               glRenderer = new GLRenderer(Wallpaper.this);
               glRenderer.onSurfaceCreated(gl, config);
            }
         };
         executor.execute(surfaceCreatedCommand);
         
      }
      

      
      @Override
      public void onSurfaceDestroyed(final SurfaceHolder holder) {
         
         Runnable surfaceDestroyedCommand = new Runnable() {
            public void run() {
               // Free OpenGL resources
               egl.eglMakeCurrent(glDisplay, EGL10.EGL_NO_SURFACE,
                     EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
               egl.eglDestroySurface(glDisplay, glSurface);
               egl.eglDestroyContext(glDisplay, glc);
               egl.eglTerminate(glDisplay);

            }
         };
         executor.execute(surfaceDestroyedCommand);
         
         super.onSurfaceDestroyed(holder);
      }
      

      
      @Override
      public void onSurfaceChanged(final SurfaceHolder holder,
            final int format, final int width, final int height) {
         super.onSurfaceChanged(holder, format, width, height);
         
         Runnable surfaceChangedCommand = new Runnable() {
            public void run() {
               glRenderer.onSurfaceChanged(gl, width, height);
            }
         };
         executor.execute(surfaceChangedCommand);
         
      }
      

      
      @Override
      public void onVisibilityChanged(final boolean visible) {
         super.onVisibilityChanged(visible);
         
         if (visible) {
            executor.execute(drawCommand);
         }
         
      }
      

      
      @Override
      public void onOffsetsChanged(final float xOffset,
            final float yOffset, final float xOffsetStep,
            final float yOffsetStep, final int xPixelOffset,
            final int yPixelOffset) {
         super.onOffsetsChanged(xOffset, yOffset, xOffsetStep,
               yOffsetStep, xPixelOffset, yPixelOffset);
         
         Runnable offsetsChangedCommand = new Runnable() {
            public void run() {
               if (xOffsetStep != 0f) {
                  glRenderer.setParallax(xOffset - 0.5f);
               }
            }
         };
         executor.execute(offsetsChangedCommand);
         
      }
      
      
   }
   

   @Override
   public Engine onCreateEngine() {
      return new MyEngine();
   }
}

