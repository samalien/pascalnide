package com.duy.pascal.frontend.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.duy.pascal.backend.core.PascalCompiler;
import com.duy.pascal.backend.debugable.DebugListener;
import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.frontend.DLog;
import com.duy.pascal.frontend.R;
import com.duy.pascal.frontend.alogrithm.InputData;
import com.duy.pascal.frontend.code.CompileManager;
import com.duy.pascal.frontend.code.ExceptionManager;
import com.duy.pascal.frontend.dialog.DialogManager;
import com.duy.pascal.frontend.file.ApplicationFileManager;
import com.duy.pascal.frontend.view.exec_screen.console.ConsoleView;
import com.duy.pascal.frontend.view.exec_screen.console.StringCompare;
import com.js.interpreter.ast.FunctionDeclaration;
import com.js.interpreter.ast.VariableDeclaration;
import com.js.interpreter.ast.codeunit.PascalProgram;
import com.js.interpreter.core.ScriptSource;
import com.js.interpreter.runtime.codeunit.RuntimeExecutable;
import com.js.interpreter.runtime.exception.RuntimePascalException;
import com.js.interpreter.runtime.exception.ScriptTerminatedException;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.duy.pascal.frontend.alogrithm.InputData.MAX_INPUT;


public class ExecuteActivity extends AbstractExecActivity implements DebugListener {
    public static final boolean DEBUG = DLog.DEBUG;
    private static final String TAG = ExecuteActivity.class.getSimpleName();
    private static final int NEW_INPUT = 1;
    private static final int NEW_OUTPUT_CHAR = 2;
    private static final int NEW_OUTPUT_STRING = 3;
    private static final int COMPLETE = 4;
    private static final int RUNTIME_ERROR = 5;
    private static final int SHOW_KEYBOARD = 6;
    public String input = "";
    private String filePath;
    private AtomicBoolean mIsRunning = new AtomicBoolean(true);
    private AtomicBoolean isCanRead = new AtomicBoolean(false);
    private RuntimeExecutable program;
    private String programFile;
    private ApplicationFileManager mFileManager;
    private Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!mIsRunning.get()) return;
            switch (msg.what) {
                case RUNTIME_ERROR:
                    onError((Exception) msg.obj);
                    break;
                case COMPLETE:
                    showDialogComplete();
                    break;
                case SHOW_KEYBOARD:
                    showKeyBoard();
                    break;
            }
        }
    };

    Runnable runnableRunProgram = new Runnable() {
        @Override
        public void run() {
            try {
                //compile
                PascalCompiler pascalCompiler = new PascalCompiler(ExecuteActivity.this);
                try {
                    PascalProgram pascalProgram = pascalCompiler.loadPascal(programFile,
                            new FileReader(programFile),
                            new ArrayList<ScriptSource>(), new ArrayList<ScriptSource>(),
                            ExecuteActivity.this);
                    program = pascalProgram.run();
//                    program.enableDebug();
                    program.run();
                    mMessageHandler.sendEmptyMessage(COMPLETE);
                } catch (ScriptTerminatedException e) {
                    mMessageHandler.sendEmptyMessage(COMPLETE);
                } catch (RuntimePascalException | ParsingException e) {
                    mMessageHandler.sendMessage(mMessageHandler.obtainMessage(RUNTIME_ERROR, e));
                }
            } catch (final Exception e) {
                mMessageHandler.sendMessage(mMessageHandler.obtainMessage(RUNTIME_ERROR, e));
            }
        }
    };

    private Runnable runnableInput = new Runnable() {
        @Override
        public void run() {
            int exitFlag;
            String c;
            InputData inputData = new InputData();
            inputData.first = 0;
            inputData.last = 0;
            exitFlag = 0;
            do {
                c = mConsoleView.readString();
//                System.out.println(c);
                switch (c) {
                    case ConsoleView.THE_ENTER_KEY: // return
                    case "\n":
                        exitFlag = 1;
                        break;
                    case ConsoleView.THE_DELETE_COMMAND:
                        if (inputData.last > 0) {
                            inputData.last--;
                            mConsoleView.commitString(String.valueOf(c));
                        }
                        break;
                    default:
                        if ((StringCompare.greaterEqual(c, " ")) && (inputData.last < MAX_INPUT)) {
                            inputData.data[inputData.last++] = c;
                            mConsoleView.commitString(String.valueOf(c));
                        }
                        break;
                }
            } while (exitFlag == 0 && isCanRead.get());
            mConsoleView.commitString("\n"); //return new line
            input = inputData.toString();
            isCanRead.set(false);
        }

    };
    private Thread runThread = new Thread(runnableRunProgram);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileManager = new ApplicationFileManager(this);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            filePath = extras.getString(CompileManager.FILE_PATH);
            if (filePath == null || filePath.isEmpty()) return;
            File file = new File(filePath);
            if (!file.exists()) {
                finish();
                return;
            }
            setTitle(file.getName());
            createAndRunProgram(filePath);
        } else {
            finish();
        }
    }

    /**
     * exec program, run program in internal memory
     *
     * @param path - file pas
     */
    private void createAndRunProgram(final String path) {
        String code = mFileManager.readFileAsString(path);

        //clone it to internal storage
        programFile = mFileManager.setContentFileTemp(code);
        mConsoleView.commitString("execute file: " + filePath + "\n");
        mConsoleView.commitString("---------------------------" + "\n");
        mMessageHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runThread.start();
            }
        }, 200);
    }

    private void showDialogComplete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.complete)
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mMessageHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 100);
                    }
                })
                .setNegativeButton(R.string.view_console, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        try {
            builder.create().show();
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsRunning.set(false);
        //stop program
        stopProgram();
    }

    /**
     * show error compile or runtime
     */
    public void onError(Exception e) {
        ExceptionManager exceptionManager = new ExceptionManager(this);
        DialogManager.createDialog(this, "Runtime error", exceptionManager.getMessage(e)).show();
        //debug
        if (DEBUG) e.printStackTrace();
    }

    public char readKey() {
        mConsoleView.setFilterKey(true);
        char res = mConsoleView.readKey();
        mConsoleView.setFilterKey(false);
        return res;
    }

    public synchronized void startInput() {
        if (DLog.DEBUG) Log.d(TAG, "startInput: ");
        mMessageHandler.sendEmptyMessage(SHOW_KEYBOARD);
        isCanRead.set(true);
        new Thread(runnableInput).start();
    }

    public boolean isInputting() {
        return isCanRead.get();
    }

    /**
     * set text console color
     *
     * @param textColor
     */
    public void setTextColor(final int textColor) {
        mConsoleView.setConsoleTextColor(textColor);
    }

    /**
     * set background console
     *
     * @param color
     */
    public void setTextBackground(final int color) {
        mConsoleView.setConsoleTextBackground(color);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    public String getInput() {
        return input;
    }

    public ConsoleView getConsoleView() {
        return mConsoleView;
    }

    @Override
    public void onGlobalVariableChangeValue(VariableDeclaration variableDeclaration) {
    }

    @Override
    public void onLocalVariableChangeValue(VariableDeclaration variableDeclaration) {
    }

    @Override
    public void onFunctionCall(final FunctionDeclaration functionDeclaration) {
        mMessageHandler.post(new Runnable() {
            @Override
            public void run() {
//                debugView.addLine(new DebugItem(DebugItem.TYPE_MSG, ">_ " + "Call procedure \'"
//                        + functionDeclaration.getName() + "\'"));
            }
        });
    }

    @Override
    public void onProcedureCall(final FunctionDeclaration functionDeclaration) {
        if (DLog.DEBUG) Log.d(TAG, "onProcedureCall: " + functionDeclaration.getName());
        mMessageHandler.post(new Runnable() {
            @Override
            public void run() {
//                debugView.addLine(new DebugItem(DebugItem.TYPE_MSG, ">_ " + "Call function \'"
//                        + functionDeclaration.getName() + "\'"));
            }
        });
    }

    @Override
    public void onNewMessage(final String msg) {
        mMessageHandler.post(new Runnable() {
            @Override
            public void run() {
//                debugView.addLine(new DebugItem(DebugItem.TYPE_MSG, ">_ " + msg));
            }
        });
    }

    @Override
    public void onClearDebug() {
////        debugView.clear();
    }

    @Override
    public void onVariableChangeValue(final String name, final Object value) {
        mMessageHandler.post(new Runnable() {
            @Override
            public void run() {
////                debugView.addLine(new DebugItem(DebugItem.TYPE_VAR, name, String.valueOf(value)));
            }
        });
    }

    @Override
    public void onFunctionCall(final String name) {
        mMessageHandler.post(new Runnable() {
            @Override
            public void run() {
////                debugView.addLine(new DebugItem(DebugItem.TYPE_MSG, "> " + "Call procedure \'" + name + "\'"));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_next_line) {
            //do something
        } else if (item.getItemId() == R.id.action_rerun) {
            CompileManager.execute(this, filePath);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * force stop
     */
    private void stopProgram() {
        Log.d(TAG, "stopProgram: ");
        try {
            //stop in put thread
            isCanRead.set(false);
            program.terminate();
            Toast.makeText(this, "Program is stopped", Toast.LENGTH_SHORT).show();
        } catch (Exception ignored) {
            if (DLog.DEBUG) Log.d(TAG, "onStop: Program is stopped");
        }
    }


}

