package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Button addButton;
    private AlertDialog taskDialog;
    private LinearLayout taskContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.add);
        taskContainer = findViewById(R.id.container);

        buildTaskDialog();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskDialog.show();
            }
        });
    }

    private void buildTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText taskEditText = dialogView.findViewById(R.id.nameEdit);
        final Spinner prioritySpinner = dialogView.findViewById(R.id.prioritySpinner);
        final Button dueDateButton = dialogView.findViewById(R.id.dueDateButton);
        final EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEdit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(dueDateButton);
            }
        });

        builder.setView(dialogView)
                .setTitle("Enter your Task")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskName = taskEditText.getText().toString().trim();
                        String taskPriority = prioritySpinner.getSelectedItem().toString();
                        String taskDueDate = dueDateButton.getText().toString().trim();
                        String taskDescription = descriptionEditText.getText().toString().trim();

                        if (!taskName.isEmpty() && !taskPriority.isEmpty() && !taskDueDate.isEmpty() && !taskDescription.isEmpty()) {
                            addTaskCard(taskName, taskPriority, taskDueDate, taskDescription, false);
                            taskEditText.setText("");  // Clear the input field for the next task
                            prioritySpinner.setSelection(0);  // Reset the spinner to the first item
                            dueDateButton.setText("Select Due Date");  // Reset the date button
                            descriptionEditText.setText("");
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        taskDialog = builder.create();
    }

    private void showDatePickerDialog(final Button dueDateButton) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                dueDateButton.setText(date);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void addTaskCard(final String taskName, final String taskPriority, final String taskDueDate, final String taskDescription, boolean isCompleted) {
        final View taskCardView = getLayoutInflater().inflate(R.layout.card, null);

        TextView taskNameView = taskCardView.findViewById(R.id.name);
        TextView taskPriorityView = taskCardView.findViewById(R.id.priority);
        TextView taskDueDateView = taskCardView.findViewById(R.id.due_date);
        TextView taskDescriptionView = taskCardView.findViewById(R.id.description);
        CheckBox taskCompletionCheckBox = taskCardView.findViewById(R.id.completion_status);
        Button editButton = taskCardView.findViewById(R.id.edit);
        Button deleteButton = taskCardView.findViewById(R.id.delete);

        taskNameView.setText(taskName);
        taskPriorityView.setText(taskPriority);
        taskDueDateView.setText("Due Date: " + taskDueDate);
        taskDescriptionView.setText(taskDescription);
        taskCompletionCheckBox.setChecked(isCompleted);

        taskCompletionCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            taskNameView.setPaintFlags(isChecked ? taskNameView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG : taskNameView.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
        });

        editButton.setOnClickListener(v -> {
            editTask(taskName, taskPriority, taskDueDate, taskDescription, taskCardView);
        });

        deleteButton.setOnClickListener(v -> {
            taskContainer.removeView(taskCardView);
        });

        taskContainer.addView(taskCardView);
    }

    private void editTask(String taskName, String taskPriority, String taskDueDate, String taskDescription, final View taskCardView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText taskEditText = dialogView.findViewById(R.id.nameEdit);
        final Spinner prioritySpinner = dialogView.findViewById(R.id.prioritySpinner);
        final Button dueDateButton = dialogView.findViewById(R.id.dueDateButton);
        final EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEdit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        taskEditText.setText(taskName);
        prioritySpinner.setSelection(((ArrayAdapter) prioritySpinner.getAdapter()).getPosition(taskPriority));
        dueDateButton.setText(taskDueDate);
        descriptionEditText.setText(taskDescription);

        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(dueDateButton);
            }
        });

        builder.setView(dialogView)
                .setTitle("Edit Task")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newTaskName = taskEditText.getText().toString().trim();
                        String newTaskPriority = prioritySpinner.getSelectedItem().toString();
                        String newTaskDueDate = dueDateButton.getText().toString().trim();
                        String newTaskDescription = descriptionEditText.getText().toString().trim();

                        if (!newTaskName.isEmpty() && !newTaskPriority.isEmpty() && !newTaskDueDate.isEmpty() && !newTaskDescription.isEmpty()) {
                            TextView taskNameView = taskCardView.findViewById(R.id.name);
                            TextView taskPriorityView = taskCardView.findViewById(R.id.priority);
                            TextView taskDueDateView = taskCardView.findViewById(R.id.due_date);
                            TextView taskDescriptionView = taskCardView.findViewById(R.id.description);

                            taskNameView.setText(newTaskName);
                            taskPriorityView.setText(newTaskPriority);
                            taskDueDateView.setText("Due Date: " + newTaskDueDate);
                            taskDescriptionView.setText(newTaskDescription);
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }
}
