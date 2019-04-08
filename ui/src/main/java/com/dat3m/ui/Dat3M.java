package com.dat3m.ui;

import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.wmm.Wmm;
import com.dat3m.ui.editor.EditorsPane;
import com.dat3m.ui.editor.Editor;
import com.dat3m.ui.editor.EditorCode;
import com.dat3m.ui.icon.IconCode;
import com.dat3m.ui.icon.IconHelper;
import com.dat3m.ui.listener.EditorListener;
import com.dat3m.ui.graph.GraphOption;
import com.dat3m.ui.options.Options;
import com.dat3m.ui.options.OptionsPane;
import com.dat3m.ui.options.utils.ControlCode;
import com.dat3m.ui.result.Dat3mResult;
import com.dat3m.ui.result.PortabilityResult;
import com.dat3m.ui.result.ReachabilityResult;
import com.dat3m.ui.utils.Task;
import javax.swing.*;

import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Token;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.dat3m.ui.editor.EditorCode.SOURCE_MM;
import static com.dat3m.ui.editor.EditorCode.TARGET_MM;
import static com.dat3m.ui.utils.Utils.showError;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.UIManager.getDefaults;

public class Dat3M extends JFrame implements ActionListener {

	private final OptionsPane optionsPane = new OptionsPane();
	private final EditorsPane editorsPane = new EditorsPane();
	private final GraphOption graph = new GraphOption();
	
	private Dat3mResult testResult;

	private Dat3M() {
		getDefaults().put("SplitPane.border", createEmptyBorder());

		setTitle("Dat3M");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setIconImage(IconHelper.getIcon(IconCode.DAT3M).getImage());

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(editorsPane.getMenu());
		setJMenuBar(menuBar);

		JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionsPane, editorsPane.getMainPane());
		mainPane.setDividerSize(2);
		add(mainPane);

		// EditorsPane needs to know if task is changed in order to show / hide source model editor
		optionsPane.getTaskPane().addActionListener(editorsPane);

		// ArchPane needs to know which program format has been loaded by editor in order to show / hide target
		editorsPane.getEditor(EditorCode.PROGRAM).addActionListener(optionsPane.getArchManager());

		// Start listening to button events
		optionsPane.getTestButton().addActionListener(this);
		optionsPane.getClearButton().addActionListener(this);
		optionsPane.getGraphButton().addActionListener(this);
		optionsPane.getRelsButton().addActionListener(this);

		// optionsPane needs to listen to editor to clean the console
		editorsPane.getEditor(EditorCode.PROGRAM).addActionListener(optionsPane);
		editorsPane.getEditor(EditorCode.SOURCE_MM).addActionListener(optionsPane);
		editorsPane.getEditor(EditorCode.TARGET_MM).addActionListener(optionsPane);

		// The console shall be cleaned every time the program or MM is modified from the editor
    	EditorListener listener = new EditorListener(optionsPane.getConsolePane(), optionsPane.getClearButton());
    	editorsPane.getEditor(EditorCode.PROGRAM).getEditorPane().addKeyListener(listener);
    	editorsPane.getEditor(EditorCode.TARGET_MM).getEditorPane().addKeyListener(listener);
    	editorsPane.getEditor(EditorCode.SOURCE_MM).getEditorPane().addKeyListener(listener);

		pack();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			Dat3M app = new Dat3M();
			app.setVisible(true);
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
	    String command = event.getActionCommand();
	    if(ControlCode.TEST.actionCommand().equals(command)){
            runTest();
            if(testResult != null){
                optionsPane.getConsolePane().setText(testResult.getVerdict());
                if(optionsPane.getGraphButton().isSelected()) {
                    EventQueue.invokeLater(graph::open);                	
                }
            }
        } else if(ControlCode.RELS.actionCommand().equals(command)){
        	try {
        		editorsPane.getEditor(TARGET_MM).load();
        		graph.getSelector().setTMM((Wmm) editorsPane.getEditor(TARGET_MM).getLoaded());
        		editorsPane.getEditor(SOURCE_MM).load();
        		graph.getSelector().setSMM((Wmm) editorsPane.getEditor(SOURCE_MM).getLoaded());        		
        	} catch (Exception e) {
				// Nothing to be done
			}
        	graph.getSelector().open();
        }
	}

	private void runTest(){
		Options options = optionsPane.getOptions();
        testResult = null;
	    try {
			Editor programEditor = editorsPane.getEditor(EditorCode.PROGRAM);
			programEditor.load();
			Program program = (Program) programEditor.getLoaded();
            try {
                Editor targetEditor = editorsPane.getEditor(EditorCode.TARGET_MM);
                targetEditor.load();
				Wmm targetModel = (Wmm) targetEditor.getLoaded();
                if(options.getTask() == Task.REACHABILITY){
                    testResult = new ReachabilityResult(program, targetModel, options, graph);
                } else {
                    try {
                    	if(!programEditor.getLoadedFormat().equals("pts")) {
                    		showError("PORTHOS only supports *.pts files", "Loading error");
                    		return;
                    	}
                        Program sourceProgram = (Program) programEditor.getLoaded();
                        Editor sourceEditor = editorsPane.getEditor(EditorCode.SOURCE_MM);
                        sourceEditor.load();
						Wmm sourceModel = (Wmm) sourceEditor.getLoaded();
                        testResult = new PortabilityResult(sourceProgram, program, sourceModel, targetModel, options, graph);
                    } catch (Exception e){
                    	String msg = e.getMessage() == null? "Memory model cannot be parsed" : e.getMessage();
                        showError(msg, "Source memory model error");
                    }
                }
            } catch (Exception e){
            	String msg = e.getMessage() == null? "Memory model cannot be parsed" : e.getMessage();
                showError(msg, "Target memory model error");
            }
        } catch (Exception e){
        	String msg = e.getMessage() == null? "Program cannot be parsed" : e.getMessage();
        	Throwable cause = e.getCause();
			if(cause instanceof InputMismatchException) {
        		Token token = ((InputMismatchException)cause).getOffendingToken();
				msg = "Problem with \"" + token.getText() + "\" at line " + token.getLine();
        	}
        	showError(msg, "Program error");
        }
	    if(testResult != null && testResult.isSat()) {
            graph.generate(testResult);
	    }
	}
}
