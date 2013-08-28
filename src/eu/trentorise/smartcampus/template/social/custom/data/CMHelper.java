/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.template.social.custom.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import eu.trentorise.smartcampus.ac.AACException;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.ac.embedded.EmbeddedSCAccessProvider;
import eu.trentorise.smartcampus.profileservice.BasicProfileService;
import eu.trentorise.smartcampus.profileservice.model.BasicProfile;
import eu.trentorise.smartcampus.social.model.Group;
import eu.trentorise.smartcampus.social.model.Groups;
import eu.trentorise.smartcampus.social.model.User;
import eu.trentorise.smartcampus.socialservice.SocialService;
import eu.trentorise.smartcampus.socialservice.SocialServiceException;
import eu.trentorise.smartcampus.template.social.Constants;

public class CMHelper {

	private static CMHelper instance = null;

	private static SCAccessProvider accessProvider = new EmbeddedSCAccessProvider();

	private Context mContext;
	private BasicProfileService profileServce = new BasicProfileService(eu.trentorise.smartcampus.template.Constants.AUTH_URL);
	private SocialService socialServce = new SocialService(eu.trentorise.smartcampus.template.Constants.SOCIAL_URL);

	private static BasicProfile profile;
	private static List<Group> savedGroups;

	public static void init(Context mContext) {
		instance = new CMHelper(mContext);
	}

	public static boolean isInitialized() {
		return instance != null;
	}
	
	public static void setProfile(BasicProfile profile) {
		CMHelper.profile = profile;
	}

	public static BasicProfile getProfile() {
		return profile;
	}

	private static CMHelper getInstance() throws Exception {
		if (instance == null)
			throw new Exception("CMHelper is not initialized");
		return instance;
	}

	public static SCAccessProvider getAccessProvider() {
		return accessProvider;
	}

	protected CMHelper(Context mContext) {
		super();
		this.mContext = mContext;
	}


	public static void load() throws Exception {
		String token = getToken();
		profile = getInstance().profileServce.getBasicProfile(token);
		readGroups(token);
	}

	/**
	 * @param token
	 * @throws SocialServiceException
	 * @throws Exception
	 */
	private static void readGroups(String token) throws SocialServiceException,
			Exception {
		Groups groups = getInstance().socialServce.getUserGroups(token);
		savedGroups = groups.getContent();
		if (savedGroups == null) {
			savedGroups = new ArrayList<Group>();
		}
		for (Iterator<Group> iterator = savedGroups.iterator(); iterator.hasNext();) {
			Group g = iterator.next();
			if (Constants.MY_PEOPLE_GROUP_ID.endsWith(g.getSocialId())) {
				iterator.remove();
			}
		} 
	}

	/**
	 * @return
	 * @throws AACException
	 * @throws Exception
	 */
	private static String getToken() throws AACException, Exception {
		return accessProvider.readToken(getInstance().mContext, eu.trentorise.smartcampus.template.Constants.CLIENT_ID, eu.trentorise.smartcampus.template.Constants.CLIENT_SECRET);
	}


	public static List<BasicProfile> getPeople(String search) throws Exception {
		String token = getToken();
		List<BasicProfile> users = getInstance().profileServce.getBasicProfiles(search,token);
		return users;
	}

	public static Group saveGroup(Group group) throws Exception {
		Group newGroup = group;
		String token = getToken();
		if (group.getSocialId() == null) {
			newGroup = getInstance().socialServce.createUserGroup(token, group.getName());
		} else {
			getInstance().socialServce.updateUserGroup(token, group);
		}
		readGroups(token);
		return newGroup;
	}

	public static void deleteGroup(Group group) throws Exception {
		String token = getToken();
		getInstance().socialServce.deleteUserGroup(token, group.getSocialId());
		readGroups(token);
	}	


	public static void endAppFailure(Activity activity, int id) {
		Toast.makeText(activity, activity.getResources().getString(id),
				Toast.LENGTH_LONG).show();
		activity.finish();
	}

	public static void showFailure(Activity activity, int id) {
		Toast.makeText(activity, activity.getResources().getString(id),
				Toast.LENGTH_LONG).show();
	}

	public static void assignToGroups(User user, Collection<Group> groups) throws Exception {
		String token = getToken();
		List<String> userIds = Collections.singletonList(user.getSocialId());
		for (Group g : groups) {
			getInstance().socialServce.addUsersToGroup(g.getSocialId(), userIds, token);
		}
		// TODO contains does not work
		for (Group g : savedGroups) {
			if (g.getUsers().contains(user) && !groups.contains(g)) {
				getInstance().socialServce.removeUsersFromGroup(g.getSocialId(), userIds, token);
			}
		}

		readGroups(token);
	}

	public static List<Group> getGroups() {
		return savedGroups != null ? savedGroups : Collections.<Group>emptyList();
	}

	public static Set<String> getUserGroups(User user) {
		Set<String> res = new HashSet<String>();
		if (getGroups() != null) {
			for (Group g : getGroups()) {
				if (g.getUsers() != null && g.getUsers().contains(user)) res.add(g.getSocialId());
			}
		}
		return res;
	}
	
	public static User toUser(BasicProfile bp) {
		User user = new User();
		user.setId(bp.getUserId());
		user.setName(bp.getName());
		user.setSocialId(bp.getSocialId());
		user.setSurname(bp.getSurname());
		return user;
	}
}
