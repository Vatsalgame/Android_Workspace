package com.teamvat.budgetme;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.teamvat.budgetme.BudgetReaderContract.BudgetEntry;

public class HomePage extends Activity {
	
	// declaring a DB handler
	BudgetDbHelper bDbHelper;
	SharedPreferences fieldValues;
	SharedPreferences.Editor fieldEdit;
	int appLaunchCount;
	boolean reviewed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		
		TextView monStats = (TextView) findViewById(R.id.monStats);
		monStats.setTypeface(null, Typeface.BOLD_ITALIC);
		updateFields();
		fieldValues = PreferenceManager.getDefaultSharedPreferences(this);		
		
		if(fieldValues.getBoolean("dailyStats", false)) {
			dailyStatsUpdate();
		}
		else {
			clearDailyStats();
		}
		askForReview();
	}
	
	// if activity is stopped and started again
	@Override
	public void onRestart() {
		super.onRestart();
		updateFields();
		if(fieldValues.getBoolean("dailyStats", false)) {
			dailyStatsUpdate();
		}
		else {
			clearDailyStats();
		}
//		askForReview();
	}
	
	// if activity is paused and started again
	@Override
	public void onResume() {
		super.onResume();
		updateFields();
		if(fieldValues.getBoolean("dailyStats", false)) {
			dailyStatsUpdate();
		}
		else {
			clearDailyStats();
		}
//		askForReview();
	}
	
	public void askForReview() {
		fieldValues = PreferenceManager.getDefaultSharedPreferences(this);
		fieldEdit = fieldValues.edit();
		// getting the number of app launches to prompt the user for a review
		appLaunchCount = fieldValues.getInt("appLaunches", 0);
		reviewed = fieldValues.getBoolean("reviewed", false);
		appLaunchCount++;
		if ((appLaunchCount % 5 == 0) && (!reviewed) && appLaunchCount != 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("It seems that you've been using this app for a while (" +
								appLaunchCount + " times). It would be really great if you'd take a moment to rate it");
			builder.setCancelable(false);
			builder.setPositiveButton("Sure, I'd love to!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// fire an intent go to your next activity
					reviewed = true;
					fieldEdit.putBoolean("reviewed", reviewed);
					fieldEdit.commit();
					// launching the app page to enter a review
					final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
					final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

					if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0)
					{
					    startActivity(rateAppIntent);
					}
					else
					{
						Context context = getApplicationContext();
						String msg = "Error in opening the app page in Play Store. Sorry";
						// msg stays for 3.5 sec instead of 2 sec
						int duration = Toast.LENGTH_SHORT;
						Toast.makeText(context, msg, duration).show();
					}
				}
			});
			builder.setNegativeButton("Nah, Sorry!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// fire an intent go to your next activity
					dialog.cancel();
					reviewed = true;
					fieldEdit.putBoolean("reviewed", reviewed);
					fieldEdit.commit();
				}
			});
			builder.setNeutralButton("Maybe later!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// fire an intent go to your next activity
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		fieldEdit.putInt("appLaunches", appLaunchCount);
		fieldEdit.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_page, menu);
		menu.clear();
		return true;
	}
	
	/** Called when user clicks on Add Your Expenses **/
	public void switchToAdd(View view) {
		Intent switchToAdd = new Intent(this, AddExpense.class);
		startActivity(switchToAdd);
	}
	
	/** Called when user clicks on Configure **/
	public void switchToConfig(View view) {
		Intent switchToConfig = new Intent(this, Configure.class);
		startActivity(switchToConfig);
	}
	
	/** Called when user clicks on Track Status **/
	public void switchToStats(View view) {
		Intent switchToStats= new Intent(this, TrackStatus.class);
		startActivity(switchToStats);
	}
	
	/** Called when user clicks on Currency Converter **/
	public void switchToCurConverter(View view) {
		Intent switchToConv = new Intent(this, CurrencyConverter.class);
		startActivity(switchToConv);
	}
	
	// to test DB reading
	// not deleting the method yet
//	public void refresh(View view) {
//		bDbHelper = new BudgetDbHelper(getApplicationContext());
//		SQLiteDatabase db = bDbHelper.getReadableDatabase();
//		// columns to read
//		String[] projection = {
//				BudgetEntry.COLUMN_NAME_EXPENSE_ID,
//				BudgetEntry.COLUMN_NAME_EXPENSE_AMT,
//				BudgetEntry.COLUMN_NAME_EXPENSE_CAT,
//				BudgetEntry.COLUMN_NAME_EXPENSE_DATE
//		};
//		
//		String selection = "Expense_Id > ?";
//		String[] selectionArgs = {
//				"0"
//		};
//		
//		Cursor rowPointer = db.query(BudgetEntry.TABLE_NAME, 
//									 projection, 
//									 selection, 
//									 selectionArgs, 
//									 null, 
//									 null, 
//									 null);
//		
////		rowPointer.moveToFirst();
//		int id = 0;
//		Double amt = 0.0;
//		String category = "default";
//		String date = "0000-00-00";
//		while (rowPointer.moveToNext()) {
//			id = rowPointer.getInt(rowPointer.getColumnIndex(BudgetEntry.COLUMN_NAME_EXPENSE_ID));
//			amt = rowPointer.getDouble(rowPointer.getColumnIndex(BudgetEntry.COLUMN_NAME_EXPENSE_AMT));
//			category = rowPointer.getString(rowPointer.getColumnIndex(BudgetEntry.COLUMN_NAME_EXPENSE_CAT));
//			date = rowPointer.getString(rowPointer.getColumnIndex(BudgetEntry.COLUMN_NAME_EXPENSE_DATE));
//		}
//		
//		TextView idText = (TextView) findViewById(R.id.idText);
//		idText.setText("" + id);
//		TextView amtText = (TextView) findViewById(R.id.amtText);
//		amtText.setText("" + amt);
//		TextView catText = (TextView) findViewById(R.id.catText);
//		catText.setText(category);
//		TextView dateText = (TextView) findViewById(R.id.dateText);
//		dateText.setText(date);
//	}
	
	
	// daily stats updater
	public void dailyStatsUpdate() {
		// getting the currency
		fieldValues = PreferenceManager.getDefaultSharedPreferences(this);
		String currency = fieldValues.getString("currency", "CAD");
		
		TextView dailyStatText = (TextView) findViewById(R.id.dailyStat);
		dailyStatText.setText("Daily Stats:");
		dailyStatText.setTypeface(null, Typeface.BOLD_ITALIC);
		
		fieldValues = PreferenceManager.getDefaultSharedPreferences(this);
		Float monthlyBudget = fieldValues.getFloat("monthlyBudget", 0.00f);
		
		TextView dayBudgetText = (TextView) findViewById(R.id.dayBudget);
		Float dailyBudget = monthlyBudget/30;
		dayBudgetText.setText("Budget for the Day: " + new DecimalFormat("#.##").format(dailyBudget) + " " + currency);
		bDbHelper = new BudgetDbHelper(getApplicationContext());
		SQLiteDatabase db = bDbHelper.getReadableDatabase();
		// getting the date to get monthly expenditure
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Date curr_date = new Date();
    	String date = dateFormat.format(curr_date);
    	// readying the query to get daily stats
    	String[] projection = {
    		"sum("	+ BudgetEntry.COLUMN_NAME_EXPENSE_AMT + ") AS 'SUM'"
    	};
    	String selection = "Expense_Date like ?";
    	String[] selectionArgs = {
    			date
    	};
    	Cursor rowPointer = db.query(BudgetEntry.TABLE_NAME, 
				 projection, 
				 selection, 
				 selectionArgs, 
				 null, 
				 null, 
				 null);
    	Double totalDailyExpense = 0.00;
    	while (rowPointer.moveToNext()) {
    		totalDailyExpense = rowPointer.getDouble(rowPointer.getColumnIndex("SUM"));
    	}
    	TextView daySpentText = (TextView) findViewById(R.id.daySpent);
    	// color coding the spent money stats
    	String spentMoney = new DecimalFormat("#.##").format(totalDailyExpense).toString();
    	SpannableStringBuilder spentBuilder = new SpannableStringBuilder();
    	SpannableString spentYellow = new SpannableString(spentMoney);
    	spentYellow.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, spentMoney.length(), 0);
    	spentBuilder.append("Money Spent: ");
    	spentBuilder.append(spentYellow);
    	spentBuilder.append(" " + currency);
//		daySpentText.setText("Money Spent: " + spentRed + " " + currency);
    	daySpentText.setText(spentBuilder);
		TextView dayRemText = (TextView) findViewById(R.id.dayRem);
		// color coding remaining money stats
		String remMoney = new DecimalFormat("#.##").format(dailyBudget - totalDailyExpense).toString();
		SpannableStringBuilder remBuilder = new SpannableStringBuilder();
		SpannableString remGreen = new SpannableString(remMoney);
		if(dailyBudget - totalDailyExpense > 0)
			remGreen.setSpan(new ForegroundColorSpan(Color.GREEN), 0, remMoney.length(), 0);
		else
			remGreen.setSpan(new ForegroundColorSpan(Color.RED), 0, remMoney.length(), 0);
		remBuilder.append("Money Remaining: ");
		remBuilder.append(remGreen);
		remBuilder.append(" " + currency);
//		dayRemText.setText("Money Remaining: " + new DecimalFormat("#.##").format(dailyBudget - totalDailyExpense) + " " + currency);
		dayRemText.setText(remBuilder);
		
		db.close();
	}
	
	// to clear daily stats
	public void clearDailyStats() {
		TextView dailyStatText = (TextView) findViewById(R.id.dailyStat);
		TextView dayBudgetText = (TextView) findViewById(R.id.dayBudget);
		TextView daySpentText = (TextView) findViewById(R.id.daySpent);
		TextView dayRemText = (TextView) findViewById(R.id.dayRem);
		dailyStatText.setText("");
		dayBudgetText.setText("");
		daySpentText.setText("");
		dayRemText.setText("");
	}
	
	// to set the main fields on home page
	public void updateFields() {
		// to get budget of the month
		fieldValues = PreferenceManager.getDefaultSharedPreferences(this);
		fieldEdit = fieldValues.edit();
		
		Float monthlyBudget = fieldValues.getFloat("monthlyBudget", 0.00f);
		String currency = fieldValues.getString("currency", "CAD");
		
		TextView monBudgetText = (TextView) findViewById(R.id.mBudgetText);
		monBudgetText.setText("Budget for this month: " + new DecimalFormat("#.##").format(monthlyBudget) + " " + currency);
		// getting text size for displaying as per the resolution of the device
		fieldEdit.putFloat("stringSize", monBudgetText.getTextSize());
		fieldEdit.commit();
		// to get total expenses for the month
		bDbHelper = new BudgetDbHelper(getApplicationContext());
		SQLiteDatabase db = bDbHelper.getReadableDatabase();
		// getting the date to get monthly expenditure
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Date curr_date = new Date();
    	String date = dateFormat.format(curr_date);
    	// readying the query
    	String[] projection = {
    		"sum("	+ BudgetEntry.COLUMN_NAME_EXPENSE_AMT + ") AS 'SUM'"
    	};
    	String selection = "Expense_Date like ?";
    	String[] selectionArgs = {
    			date.substring(0, 7) + "%"
    	};
    	
    	Cursor rowPointer = db.query(BudgetEntry.TABLE_NAME, 
    								 projection, 
    								 selection, 
    								 selectionArgs, 
    								 null, 
    								 null, 
    								 null);
    	Double totalMonthlyExpense = 0.00;
    	while (rowPointer.moveToNext()) {
    		totalMonthlyExpense = rowPointer.getDouble(rowPointer.getColumnIndex("SUM"));
    	}
    	
    	TextView monExpenses = (TextView) findViewById(R.id.moneySpentText);
    	// color coding the spent money stats
    	String spentMoney = new DecimalFormat("#.##").format(totalMonthlyExpense).toString();
    	SpannableStringBuilder spentBuilder = new SpannableStringBuilder();
    	SpannableString spentYellow = new SpannableString(spentMoney);
    	spentYellow.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, spentMoney.length(), 0);
    	spentBuilder.append("Money Spent: ");
    	spentBuilder.append(spentYellow);
    	spentBuilder.append(" " + currency);
//    	monExpenses.setText("Money Spent: " + new DecimalFormat("#.##").format(totalMonthlyExpense) + " " + currency);
    	monExpenses.setText(spentBuilder);
    	
    	Double moneyRemaining = monthlyBudget - totalMonthlyExpense;
    	TextView remText = (TextView) findViewById(R.id.remainderText);
    	// color coding remaining money stats
    	String remMoney = new DecimalFormat("#.##").format(moneyRemaining).toString();
    	SpannableStringBuilder remBuilder = new SpannableStringBuilder();
    	SpannableString remGreen = new SpannableString(remMoney);
    	if(moneyRemaining > 0)
    		remGreen.setSpan(new ForegroundColorSpan(Color.GREEN), 0, remMoney.length(), 0);
    	else
    		remGreen.setSpan(new ForegroundColorSpan(Color.RED), 0, remMoney.length(), 0);
    	remBuilder.append("Money Remaining: ");
    	remBuilder.append(remGreen);
    	remBuilder.append(" " + currency);
//    	remText.setText("Money Remaining: " + new DecimalFormat("#.##").format(moneyRemaining) + " " + currency);
    	remText.setText(remBuilder);
    	
    	db.close();
	} 

}
