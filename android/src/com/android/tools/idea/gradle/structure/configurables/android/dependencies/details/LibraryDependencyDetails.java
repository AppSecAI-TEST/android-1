/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.android.tools.idea.gradle.structure.configurables.android.dependencies.details;

import com.android.tools.idea.gradle.structure.configurables.PsContext;
import com.android.tools.idea.gradle.structure.model.PsArtifactDependencySpec;
import com.android.tools.idea.gradle.structure.model.android.PsLibraryDependency;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.ui.HintHint;
import com.intellij.ui.HyperlinkAdapter;
import com.intellij.ui.LightweightHint;
import com.intellij.ui.components.JBLabel;
import org.jdesktop.swingx.JXLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.intellij.codeInsight.hint.HintUtil.INFORMATION_COLOR;
import static org.jetbrains.android.util.AndroidUiUtil.setUpAsHtmlLabel;

public class LibraryDependencyDetails implements DependencyDetails<PsLibraryDependency> {
  @NotNull private final PsContext myContext;

  private JPanel myMainPanel;
  private JXLabel myGroupIdLabel;
  private JXLabel myArtifactNameLabel;
  private JBLabel myResolvedVersionLabel;
  private JBLabel mySourceInfoLabel;
  private JXLabel myScopeLabel;
  private JBLabel myRequestedVersionLabel;
  private LightweightHint mySourceInfoHint;

  private PsLibraryDependency myDependency;

  public LibraryDependencyDetails(@NotNull PsContext context) {
    myContext = context;

    mySourceInfoLabel.setIcon(AllIcons.General.Information);

    mySourceInfoLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        if (mySourceInfoLabel.isVisible() && myDependency != null && (mySourceInfoHint == null || !mySourceInfoHint.isVisible())) {
          List<String> modules = myDependency.findRequestingModuleDependencies();

          int moduleCount = modules.size();
          if (moduleCount > 0) {

            StringBuilder buffer = new StringBuilder();
            buffer.append("<html>Requested by:<br/>");
            for (int i = 0; i < moduleCount; i++) {
              String moduleName = modules.get(i);
              buffer.append("<a href='").append(moduleName).append("'>").append(moduleName).append("</a>");
              if (i < moduleCount - 1) {
                buffer.append("<br/>");
              }
            }

            JEditorPane hintContents = new JEditorPane();
            setUpAsHtmlLabel(hintContents);
            hintContents.setText(buffer.toString());
            hintContents.addHyperlinkListener(new HyperlinkAdapter() {
              @Override
              protected void hyperlinkActivated(HyperlinkEvent e) {
                String moduleName = e.getDescription();
                myContext.setSelectedModule(moduleName, LibraryDependencyDetails.this);

                if (mySourceInfoHint != null) {
                  mySourceInfoHint.hide();
                  mySourceInfoHint = null;
                }
              }
            });

            mySourceInfoHint = new LightweightHint(hintContents);
          }

          HintHint hintInfo = new HintHint(e).setPreferredPosition(Balloon.Position.above)
            .setAwtTooltip(true)
            .setTextBg(INFORMATION_COLOR)
            .setShowImmediately(true);

          Point p = e.getPoint();
          mySourceInfoHint.show(myMainPanel, p.x, p.y, mySourceInfoLabel, hintInfo);
        }
      }
    });
  }

  @Override
  @NotNull
  public JPanel getPanel() {
    return myMainPanel;
  }

  @Override
  public void display(PsLibraryDependency dependency) {
    myDependency = dependency;

    PsArtifactDependencySpec declaredSpec = myDependency.getDeclaredSpec();
    assert declaredSpec != null;

    myGroupIdLabel.setText(declaredSpec.group);
    myArtifactNameLabel.setText(declaredSpec.name);
    myResolvedVersionLabel.setText(myDependency.getResolvedSpec().version);

    mySourceInfoLabel.setVisible(myDependency.hasPromotedVersion());
    mySourceInfoHint = null;

    myRequestedVersionLabel.setText(declaredSpec.version);
    myScopeLabel.setText(myDependency.getConfigurationName());
  }

  @Override
  @NotNull
  public Class<PsLibraryDependency> getSupportedModelType() {
    return PsLibraryDependency.class;
  }

  @Override
  @NotNull
  public PsLibraryDependency getModel() {
    return myDependency;
  }
}