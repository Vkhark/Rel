package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class FindReplace extends Dialog {

	private Shell shell;
	
	private Label lblFind;
	private Text textFind;
	private Pattern pattern;
	
	private Label lblReplace;
	private Text textReplace;
	
	private Composite compositeDirectionScope;
	private Group grpDirection;
	private Button btnRadioForward;
	private Button btnRadioBackward;
	
	private Composite compositeButtons;
	private Group grpScope;
	private Button btnRadioAll;
	private Button btnRadioSelected;
	
	private Composite compositeOptions;
	private Group grpOptions;
	private Button btnCheckCaseSensitive;
	private Button btnCheckWholeWord;
	private Button btnCheckRegexp;
	private Button btnCheckWrapsearch;
	private Button btnCheckIncremental;
	
	private Composite compositeVerticalBuffer;
	
	private Button btnFind;
	private Button btnReplaceFind;
	private Button btnReplace;
	private Button btnReplaceAll;

	private Composite compositeStatusAndClose;
	private Label lblStatus;
	
	private StyledText text;
	
	private int currentLine = -1;

	private LineBackgroundListener lineBackgroundListener = new LineBackgroundListener() {
		@Override
		public void lineGetBackground(LineBackgroundEvent event) {
			int line = text.getLineAtOffset(event.lineOffset);
			if (line == currentLine)
				event.lineBackground = SWTResourceManager.getColor(230, 230, 255);
		}	
	};
	
	private LineStyleListener lineStyleListener = new LineStyleListener() {
		@Override
		public void lineGetStyle(LineStyleEvent event) {
			if (textFind == null || pattern == null)
				return;
			String needle = textFind.getText().trim();
			if (needle.length() == 0)
				return;
			Vector<StyleRange> styles = new Vector<StyleRange>();
			Color color = SWTResourceManager.getColor(180, 180, 255);
			String haystack = event.lineText;
			int offset = 0;
			Matcher matcher = pattern.matcher(haystack);
			while (matcher.find(offset)) {
				int hitStart = matcher.start();
				int hitEnd = matcher.end();
				if (hitStart == hitEnd)
					break;
				StyleRange style = new StyleRange(event.lineOffset + hitStart, hitEnd - hitStart, null, color);
				styles.add(style);
				offset = hitEnd;
			}
			event.styles = styles.toArray(new StyleRange[0]);
		}
	};

	private void setStatus(String error) {
		lblStatus.setText(error);
		compositeStatusAndClose.layout();
	}

	private void compilePattern() {
		String needle = textFind.getText().trim();
		String regexp;
		if (btnCheckWholeWord.getSelection())
			regexp = "\\b(" + Pattern.quote(needle) + ")\\b";
		else if (!btnCheckRegexp.getSelection())
			regexp = Pattern.quote(needle);
		else
			regexp = needle;
		try {
			pattern = Pattern.compile(regexp, (!btnCheckCaseSensitive.getSelection()) ? Pattern.CASE_INSENSITIVE : 0);
		} catch (PatternSyntaxException pse) {
			pattern = null;
			String error = "Regex error: " + pse.getMessage();
			setStatus(error);
			return;
		}
	}
	
	private void doFind() {
		setStatus("");
		compilePattern();
		text.redraw();
	}
	
	private void doFindNext() {
		doFind();
	}
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FindReplace(Shell parent, StyledText text) {
		super(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		this.text = text;
		setText("Find/Replace");
		text.addLineBackgroundListener(lineBackgroundListener);
		text.addLineStyleListener(lineStyleListener);
		text.redraw();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setText("Find/Replace");
		shell.setLayout(new GridLayout(3, false));
	
		new Label(shell, SWT.NONE);		
		lblFind = new Label(shell, SWT.NONE);
		lblFind.setAlignment(SWT.RIGHT);
		lblFind.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFind.setText("Find:");
		textFind = new Text(shell, SWT.BORDER);
		textFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFindNext();
			}
		});
		textFind.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				compilePattern();
				doFind();
			}
		});
		textFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		new Label(shell, SWT.NONE);
		lblReplace = new Label(shell, SWT.NONE);
		lblReplace.setAlignment(SWT.RIGHT);
		lblReplace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReplace.setText("Replace with:");
		textReplace = new Text(shell, SWT.BORDER);
		textReplace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		compositeDirectionScope = new Composite(shell, SWT.NONE);
		compositeDirectionScope.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeDirectionScope.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		grpDirection = new Group(compositeDirectionScope, SWT.NONE);
		grpDirection.setText("Direction");
		
		btnRadioForward = new Button(grpDirection, SWT.RADIO);
		btnRadioForward.setBounds(10, 10, 90, 18);
		btnRadioForward.setSelection(true);
		btnRadioForward.setText("Forward");
		
		btnRadioBackward = new Button(grpDirection, SWT.RADIO);
		btnRadioBackward.setBounds(10, 34, 90, 18);
		btnRadioBackward.setText("Backward");
		
		grpScope = new Group(compositeDirectionScope, SWT.NONE);
		grpScope.setText("Scope");
		
		btnRadioAll = new Button(grpScope, SWT.RADIO);
		btnRadioAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		});
		btnRadioAll.setBounds(10, 10, 119, 18);
		btnRadioAll.setSelection(true);
		btnRadioAll.setText("All");
		
		btnRadioSelected = new Button(grpScope, SWT.RADIO);
		btnRadioSelected.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		});
		btnRadioSelected.setBounds(10, 34, 119, 18);
		btnRadioSelected.setText("Selected lines");
		
		compositeOptions = new Composite(shell, SWT.NONE);
		compositeOptions.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		grpOptions = new Group(compositeOptions, SWT.NONE);
		grpOptions.setText("Options");
		
		btnCheckCaseSensitive = new Button(grpOptions, SWT.CHECK);
		btnCheckCaseSensitive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		});
		btnCheckCaseSensitive.setBounds(10, 10, 127, 18);
		btnCheckCaseSensitive.setText("Case sensitive");
		
		btnCheckWholeWord = new Button(grpOptions, SWT.CHECK);
		btnCheckWholeWord.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		});
		btnCheckWholeWord.setBounds(10, 34, 127, 18);
		btnCheckWholeWord.setText("Whole word");
		
		btnCheckRegexp = new Button(grpOptions, SWT.CHECK);
		btnCheckRegexp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
				btnCheckIncremental.setEnabled(!btnCheckRegexp.getSelection());
				btnCheckWholeWord.setEnabled(!btnCheckRegexp.getSelection());
			}
		});
		btnCheckRegexp.setBounds(10, 58, 175, 18);
		btnCheckRegexp.setText("Regular expressions");
		
		btnCheckWrapsearch = new Button(grpOptions, SWT.CHECK);
		btnCheckWrapsearch.setBounds(144, 10, 138, 18);
		btnCheckWrapsearch.setText("Wrap search");
		
		btnCheckIncremental = new Button(grpOptions, SWT.CHECK);
		btnCheckIncremental.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		});
		btnCheckIncremental.setBounds(144, 34, 138, 18);
		btnCheckIncremental.setText("Incremental");
		
		compositeVerticalBuffer = new Composite(shell, SWT.NONE);
		GridData gd_compositeVerticalBuffer = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
		gd_compositeVerticalBuffer.heightHint = 3;
		compositeVerticalBuffer.setLayoutData(gd_compositeVerticalBuffer);
		
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		compositeButtons = new Composite(shell, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(2, true));
		compositeButtons.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, true, 1, 1));
		
		btnFind = new Button(compositeButtons, SWT.NONE);
		btnFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFindNext();
			}
		});
		btnFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnFind.setText("Find");
		
		btnReplaceFind = new Button(compositeButtons, SWT.NONE);
		btnReplaceFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnReplaceFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReplaceFind.setText("Replace/Find");
		
		btnReplace = new Button(compositeButtons, SWT.NONE);
		btnReplace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnReplace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReplace.setText("Replace");
		
		btnReplaceAll = new Button(compositeButtons, SWT.NONE);
		btnReplaceAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnReplaceAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReplaceAll.setText("Replace All");
		
		compositeStatusAndClose = new Composite(shell, SWT.NONE);
		compositeStatusAndClose.setLayout(new GridLayout(2, false));
		compositeStatusAndClose.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 3, 1));
		
		lblStatus = new Label(compositeStatusAndClose, SWT.WRAP);
		lblStatus.setText("blah");
		lblStatus.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		
		Button btnClose = new Button(compositeStatusAndClose, SWT.RIGHT);
		btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnClose.setAlignment(SWT.CENTER);
		btnClose.setText("Close");
		btnClose.setFocus();
		btnClose.setSize(btnClose.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.removeLineBackgroundListener(lineBackgroundListener);
				text.removeLineStyleListener(lineStyleListener);
				text.redraw();
				shell.dispose();
			}
		});
		
		shell.pack();
	}

	/*
	public static void main(String args[]) {
		Display display = new Display();
		Shell shell = new Shell(display);
		(new SearchReplace(shell, new StyledText(shell, SWT.NONE))).open();
	}
	*/

}
