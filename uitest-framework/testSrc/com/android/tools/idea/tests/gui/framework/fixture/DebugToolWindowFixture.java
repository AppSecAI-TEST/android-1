/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.tests.gui.framework.fixture;

import com.android.tools.idea.tests.gui.framework.GuiTests;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import org.fest.swing.core.GenericTypeMatcher;
import org.jetbrains.annotations.NotNull;

public class DebugToolWindowFixture extends ExecutionToolWindowFixture {
  public DebugToolWindowFixture(@NotNull IdeFrameFixture frameFixture) {
    super("Debug", frameFixture);
  }

  public DebugToolWindowFixture pressResumeProgram() {
    myRobot.click(findDebugResumeButton());
    return this;
  }

  public DebugToolWindowFixture waitForBreakPointHit() {
    findDebugResumeButton();
    return this;
  }

  private ActionButton findDebugResumeButton() {
    return GuiTests.waitUntilShowing(myRobot, new GenericTypeMatcher<ActionButton>(ActionButton.class) {
      @Override
      protected boolean isMatching(@NotNull ActionButton button) {
        return "com.intellij.xdebugger.impl.actions.ResumeAction".equals(button.getAction().getClass().getCanonicalName())
               && button.isEnabled();
      }
    });
  }
}