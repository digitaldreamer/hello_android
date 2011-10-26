/***
 * Excerpted from "Hello, Android! 3e",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/
package org.example.opengl;

import android.app.Activity;
import android.os.Bundle;

public class OpenGL extends Activity {
   GLView view;
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      view = new GLView(this);
      setContentView(view);
   }

   @Override
   protected void onPause() {
       super.onPause();
       view.onPause();
   }

   @Override
   protected void onResume() {
       super.onResume();
       view.onResume();
   }
}
