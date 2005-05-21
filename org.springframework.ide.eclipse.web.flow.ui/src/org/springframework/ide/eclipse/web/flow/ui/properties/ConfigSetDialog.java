/*
 * Copyright 2002-2005 the original author or authors.
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

package org.springframework.ide.eclipse.web.flow.ui.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.ide.eclipse.beans.core.BeansCorePlugin;
import org.springframework.ide.eclipse.beans.core.model.IBeansConfigSet;
import org.springframework.ide.eclipse.beans.core.model.IBeansProject;
import org.springframework.ide.eclipse.web.flow.core.WebFlowCorePlugin;
import org.springframework.ide.eclipse.web.flow.core.model.IWebFlowConfig;
import org.springframework.ide.eclipse.web.flow.core.model.IWebFlowModel;
import org.springframework.ide.eclipse.web.flow.core.model.IWebFlowProject;
import org.springframework.ide.eclipse.web.flow.ui.WebFlowUIPlugin;
import org.springframework.ide.eclipse.web.flow.ui.model.ConfigNode;
import org.springframework.ide.eclipse.web.flow.ui.model.ConfigSetNode;
import org.springframework.ide.eclipse.web.flow.ui.model.ModelLabelDecorator;
import org.springframework.ide.eclipse.web.flow.ui.model.ModelLabelProvider;
import org.springframework.ide.eclipse.web.flow.ui.model.ModelSorter;
import org.springframework.ide.eclipse.web.flow.ui.model.ProjectNode;

public class ConfigSetDialog extends Dialog {

    private static final String TITLE_NEW = "ConfigSetDialog.title.new";

    private static final String TITLE_EDIT = "ConfigSetDialog.title.edit";

    private static final String ERROR_INVALID_NAME = "ConfigSetDialog.error.invalidName";

    private static final String ERROR_USED_NAME = "ConfigSetDialog.error.usedName";

    private static final String NAME_TEXT_LABEL = "ConfigSetDialog.nameText.label";

    private static final String OVERRIDE_TEXT_LABEL = "ConfigSetDialog.overrideText.label";

    private static final String INCOMPLETE_TEXT_LABEL = "ConfigSetDialog.incompleteText.label";

    private static final int LIST_VIEWER_HEIGHT = 250;

    private static final int BEANS_CONFIG_LIST_VIEWER_HEIGHT = 80;

    private static final int LIST_VIEWER_WIDTH = 300;

    private Text nameText;

    private Button overrideButton;

    private Button incompleteButton;

    private CheckboxTableViewer configsViewer;

    private CheckboxTableViewer beansConfigSetViewer;

    private Label errorLabel;

    private Button okButton;

    private ProjectNode project;

    private ConfigSetNode configSet;

    private String configSetName;

    private String title;

    public ConfigSetDialog(Shell parent, ProjectNode project,
            String configSetName) {
        super(parent);
        this.project = project;
        this.configSetName = configSetName;
        if (configSetName == null) {
            configSet = new ConfigSetNode(project);
            title = WebFlowUIPlugin.getResourceString(TITLE_NEW);
        }
        else {
            configSet = project.getConfigSet(configSetName);
            title = WebFlowUIPlugin.getResourceString(TITLE_EDIT);
        }
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);

        // create labeled name text field
        Composite nameGroup = new Composite(composite, SWT.NULL);
        nameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        nameGroup.setLayout(layout);

        Label nameLabel = new Label(nameGroup, SWT.NONE);
        nameLabel.setText(WebFlowUIPlugin.getResourceString(NAME_TEXT_LABEL));

        nameText = new Text(nameGroup, SWT.SINGLE | SWT.BORDER);
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        nameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateName();
            }
        });

        // config set list viewer
        configsViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = LIST_VIEWER_WIDTH;
        gd.heightHint = LIST_VIEWER_HEIGHT;
        configsViewer.getTable().setLayoutData(gd);
        configsViewer.setContentProvider(new ConfigFilesContentProvider(
                createConfigList()));
        configsViewer.setLabelProvider(new DecoratingLabelProvider(
                new ModelLabelProvider(), new ModelLabelDecorator()));
        configsViewer.setSorter(new ModelSorter(true));
        configsViewer.setInput(this); // activate content provider
        configsViewer.setCheckedElements(configSet.getConfigs().toArray());

        Label beansLabel = new Label(composite, SWT.NONE);
        beansLabel
                .setText("Link Spring Beans config set to WebFlow config set");
        // config set list viewer
        beansConfigSetViewer = CheckboxTableViewer.newCheckList(composite,
                SWT.BORDER);
        gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = LIST_VIEWER_WIDTH;
        gd.heightHint = BEANS_CONFIG_LIST_VIEWER_HEIGHT;
        beansConfigSetViewer.getTable().setLayoutData(gd);
        beansConfigSetViewer.setContentProvider(new ConfigFilesContentProvider(
                createBeansConfigList()));
        beansConfigSetViewer.setLabelProvider(new DecoratingLabelProvider(
                new ModelLabelProvider(), new ModelLabelDecorator()));
        beansConfigSetViewer.setSorter(new ModelSorter(true));
        beansConfigSetViewer.setInput(this); // activate content provider
        if (configSet != null) {
            IBeansConfigSet beansConfigSet = configSet.getBeansConfigSet();
            if (beansConfigSet != null)
                beansConfigSetViewer
                        .setCheckedElements(new Object[] { beansConfigSet });
        }
        // error label
        errorLabel = new Label(composite, SWT.NONE);
        errorLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        applyDialogFont(composite);
        return composite;
    }

    protected Button createCheckBox(Composite group, String labelText) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        group.setLayout(layout);

        Button button = new Button(group, SWT.CHECK);

        Label label = new Label(group, SWT.NONE);
        label.setText(labelText);

        return button;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
        // do this here because setting the text will set enablement on the
        // ok button
        nameText.setFocus();
        String name = configSet.getName();
        if (name != null && name.trim().length() != 0) {
            nameText.setText(name);
            okButton.setEnabled(true);
        }
        else {
            okButton.setEnabled(false);
        }
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            String name = nameText.getText();

            // Update config set
            configSet.clear();
            configSet.setName(name);

            // Add selected configs to config set
            Object[] configs = configsViewer.getCheckedElements();
            for (int i = 0; i < configs.length; i++) {
                configSet.addConfig((ConfigNode) configs[i]);
            }

            // Add newly created config set to project or re-add existing one
            if (configSetName == null) {
                configSet.setParent(project);
            }
            else if (!configSetName.equals(name)) {
                project.removeConfigSet(configSetName);
            }
            
            project.addConfigSet(configSet);
            
            Object[] beansConfigSet = beansConfigSetViewer.getCheckedElements();
            if (beansConfigSet.length > 0) {
                configSet
                        .setBeansConfigSet((IBeansConfigSet) beansConfigSet[0]);
            }
            else {
                configSet.setBeansConfigSet(null);
            }
        }
        super.buttonPressed(buttonId);
    }

    private List createConfigList() {

        // Create new list with config files from this config set
        List configs = new ArrayList(configSet.getConfigs());

        // Add missing configs from project
        Iterator iter = project.getConfigs().iterator();
        while (iter.hasNext()) {
            ConfigNode config = (ConfigNode) iter.next();
            if (!configSet.hasConfig(config.getName())) {
                configs.add(new ConfigNode(configSet, config.getName()));
            }
        }

        // Add all configs from referenced projects
        IWebFlowModel model = WebFlowCorePlugin.getModel();
        try {
            IProject[] projects = project.getProject().getReferencedProjects();
            for (int i = 0; i < projects.length; i++) {
                IWebFlowProject project = model.getProject(projects[i]);
                if (project != null) {
                    iter = project.getConfigs().iterator();
                    while (iter.hasNext()) {
                        IWebFlowConfig config = (IWebFlowConfig) iter.next();
                        String path = config.getConfigPath();
                        if (!configSet.hasConfig(path)) {
                            configs.add(new ConfigNode(configSet, path));
                        }
                    }
                }
            }
        }
        catch (CoreException e) {
            // we can't do anything here
        }
        return configs;
    }

    private List createBeansConfigList() {
        IBeansProject beansProject = BeansCorePlugin.getModel().getProject(
                configSet.getProjectNode().getProject());
        // Create new list with config files from this config set
        List configs = new ArrayList(beansProject.getConfigSets());

        return configs;
    }

    private void validateName() {
        boolean isEnabled = false;

        String name = nameText.getText();
        if (name == null || name.trim().length() == 0) {
            errorLabel.setText(WebFlowUIPlugin
                    .getResourceString(ERROR_INVALID_NAME));
        }
        else if (configSetName == null || !name.equals(configSetName)) {
            if (project.hasConfigSet(name)) {
                errorLabel.setText(WebFlowUIPlugin
                        .getResourceString(ERROR_USED_NAME));
            }
            else {
                errorLabel.setText("");
                isEnabled = true;
            }
        }
        else {
            isEnabled = true;
        }

        okButton.setEnabled(isEnabled);
    }

    private class ConfigFilesContentProvider implements
            IStructuredContentProvider {
        private List configs;

        public ConfigFilesContentProvider(List configs) {
            this.configs = configs;
        }

        public Object[] getElements(Object obj) {
            return configs.toArray();
        }

        public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
        }

        public void dispose() {
        }
    }
}