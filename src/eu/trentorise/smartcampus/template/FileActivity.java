/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.template;

import java.io.ByteArrayOutputStream;

import smartcampus.android.template.standalone.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.ac.embedded.EmbeddedSCAccessProvider;
import eu.trentorise.smartcampus.filestorage.client.model.Account;
import eu.trentorise.smartcampus.filestorage.client.model.Metadata;
import eu.trentorise.smartcampus.filestorage.client.model.Resource;
import eu.trentorise.smartcampus.filestorage.client.model.StorageType;
import eu.trentorise.smartcampus.storage.AndroidFilestorage;

public class FileActivity extends Activity {

	private static final int AUTH_REQUESTCODE = 100;
	private static final int PHOTO_REQUESTCODE = 200;

	private String mAccountId = null;

	/**
	 * Provides access to the authentication mechanism. Used to retrieve the
	 * token
	 */
	private SCAccessProvider mAccessProvider = new EmbeddedSCAccessProvider();
	/** Filestorage connector reference */
	private AndroidFilestorage mFilestorage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFilestorage = new AndroidFilestorage(Constants.FILESTORAGE_SERVICE, Constants.APPID);
		mAccessProvider = new EmbeddedSCAccessProvider();

		setContentView(R.layout.file_mgmt);

		Button btn = (Button) findViewById(R.id.photo_btn);
		if (mAccountId == null) {
			btn.setEnabled(false);
		}
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, PHOTO_REQUESTCODE);
			}
		});

		// verify user account: if not present, create one
		if (mAccountId == null) {
			new AsyncTask<Void, Void, Object>(){
				@Override
				protected Object doInBackground(Void... params) {
					try {
						String token = mAccessProvider.readToken(FileActivity.this, Constants.CLIENT_ID, Constants.CLIENT_SECRET);
						Account account = mFilestorage.getAccountByUser(token);
						if (account != null) return account;
						return token;
					} catch (Exception e) {
						return null;
					}
				}
				protected void onPostExecute(Object res) {
					if (res != null && res instanceof Account) {
						mAccountId = ((Account)res).getId();
						findViewById(R.id.photo_btn).setEnabled(true);
					}
					else if (res != null) {
						try {
							mFilestorage.startAuthActivityForResult(FileActivity.this, res.toString(), StorageType.DROPBOX, AUTH_REQUESTCODE);
						} catch (Exception e) {
							Toast.makeText(FileActivity.this, "Failed to authorize storage", Toast.LENGTH_LONG).show();
							finish();
						}
					} else {
						Toast.makeText(FileActivity.this, "Failed to authorize storage", Toast.LENGTH_LONG).show();
						finish();
					}
				};
			}.execute();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// user account acquisition complete
		if (requestCode == AUTH_REQUESTCODE) {
			// user account acquired
			if (resultCode == Activity.RESULT_OK) {
				mAccountId = data.getStringExtra(AndroidFilestorage.EXTRA_OUTPUT_ACCOUNT_ID);
				Toast.makeText(
						this,
						"User account created and stored " , Toast.LENGTH_LONG)
						.show();
				
				findViewById(R.id.photo_btn).setEnabled(true);
				
				// user account cancelled
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this, "CANCELLED", Toast.LENGTH_LONG).show();
				// user account failed
			} else {
				Toast.makeText(this, "ERROR: " + resultCode, Toast.LENGTH_LONG)
						.show();
			}
		}
		// photo selected
		if (requestCode == PHOTO_REQUESTCODE) {
			if (resultCode == Activity.RESULT_OK) {
				Bitmap image = (Bitmap) data.getExtras().get("data");
				// store and re-read remotely
				new StoreFileTask().execute(image);
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class StoreFileTask extends AsyncTask<Bitmap, Void, Bitmap> {
		private ProgressDialog progress = null;

		protected Bitmap doInBackground(Bitmap... params) {
			try {
				Bitmap image = params[0];
				ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100,
						byteArrayBitmapStream);

				String token = mAccessProvider.readToken(FileActivity.this, Constants.CLIENT_ID, Constants.CLIENT_SECRET);
				// store file remotely
				Metadata metadata = mFilestorage.storeResourceByUser(
						byteArrayBitmapStream.toByteArray(),
						"image" + System.currentTimeMillis() + ".jpg",
						"image/jpg", token, mAccountId, false);

				if (metadata != null) {
					// read file remotely
					Resource resource = mFilestorage.getResourceByUser(token, metadata.getResourceId());
					if (resource == null)
						return null;
					byte[] content = resource.getContent();
					Bitmap bmp = BitmapFactory.decodeByteArray(content, 0,
							content.length);
					return bmp;
				} else
					return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (progress != null) {
				try {
					progress.cancel();
				} catch (Exception e) {
					Log.w(getClass().getName(),
							"Problem closing progress dialog: "
									+ e.getMessage());
				}
			}
			// image read correctly
			if (result != null) {
				ImageView iv = (ImageView) findViewById(R.id.photo_iv);
				iv.setImageBitmap(result);
			} else {
				Toast.makeText(FileActivity.this, "Failed storing resource!",
						Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(FileActivity.this, "",
					"Storing and reading image remotely...", true);
			super.onPreExecute();
		}
	}

}
