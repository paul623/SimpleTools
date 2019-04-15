package com.paul.simpletools.classbox.utils;

import android.util.Log;

import com.google.gson.JsonObject;
import com.paul.simpletools.classbox.model.SuperLesson;
import com.paul.simpletools.classbox.model.SuperProfile;
import com.paul.simpletools.classbox.model.SuperResult;
import com.paul.simpletools.classbox.model.SuperTerm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 超表解析器
 * Created by Liu ZhuangFei on 2018/8/10.
 */

public class SuperParser {
    private static final String TAG = "SuperParser";

    public static SuperProfile parseLoginResult(String result) {
        Log.d(TAG, "parseLoginResult: " + result);
        SuperProfile profile = new SuperProfile();
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject data = obj.getJSONObject("data");

            if(data.has("student")){
                JSONObject student = data.getJSONObject("student");

                String academyName = JsonUtils.getStringFromObj(student, "academyName", "");
                String schoolName = JsonUtils.getStringFromObj(student, "schoolName", "未知学校");
                int grade = JsonUtils.getIntFromObj(student, "grade", 0);
                int beginYear = JsonUtils.getIntFromObj(student, "beginYear", 0);
                String nickName = JsonUtils.getStringFromObj(student, "nickName", "未知");
                int term = JsonUtils.getIntFromObj(student, "term", 1);

                profile.setAcademyName(academyName);
                profile.setBeginYear(beginYear);
                profile.setNickName(nickName);
                profile.setTerm(term);
                profile.setSchoolName(schoolName);
                profile.setGrade(grade);

                JSONObject attachmentBO = student.getJSONObject("attachmentBO");
                JSONArray termList = attachmentBO.getJSONArray("myTermList");
                List<SuperTerm> superTerms = new ArrayList<>();
                for (int i = 0; i < termList.length(); i++) {
                    JSONObject termObj = termList.getJSONObject(i);
                    int begin = JsonUtils.getIntFromObj(termObj, "beginYear", 0);
                    int t = JsonUtils.getIntFromObj(termObj, "term", 1);
                    SuperTerm superTerm = new SuperTerm();
                    superTerm.setBeginYear(begin);
                    superTerm.setTerm(t);
                    superTerms.add(superTerm);
                }
                profile.setTermList(superTerms);
            }


            if (data != null && data.has("statusInt")) {
                int statusInt = data.getInt("statusInt");
                String errMsg = JsonUtils.getStringFromObj(data,"errorStr","errMsg is empty");
                if (statusInt == 0) {
                    profile.setSuccess(false);
                    profile.setErrMsg(errMsg);
                } else profile.setSuccess(true);
            }

            return profile;
        } catch (JSONException e) {
            e.printStackTrace();
            profile.setSuccess(false);
            profile.setErrMsg("parseLogin:"+e.getMessage());
            return profile;
        }
    }

    public static SuperResult parseLessonResult(String result) {
        SuperResult scanResult = new SuperResult();
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject data = obj.getJSONObject("data");
            if (data == null) {
                scanResult.setErrMsg("解析异常,不存在data字段");
                scanResult.setSuccess(false);
                return scanResult;
            }

            int status = JsonUtils.getIntFromObj(data, "statusInt", 1);
            if (status == 0) {
                scanResult.setErrMsg(JsonUtils.getStringFromObj(data, "errorStr", "未知错误"));
                scanResult.setSuccess(false);
            } else {
                JSONArray lessonList = data.getJSONArray("lessonList");
                if (lessonList == null) {
                    scanResult.setErrMsg("解析异常,不存在lessonList字段");
                    scanResult.setSuccess(false);
                    return scanResult;
                }

                List<SuperLesson> lessons = new ArrayList<>();
                for (int i = 0; i < lessonList.length(); i++) {
                    JSONObject lessonObj = lessonList.getJSONObject(i);
                    SuperLesson lesson = new SuperLesson();
                    int day = JsonUtils.getIntFromObj(lessonObj, "day", 0);
                    int sectionend = JsonUtils.getIntFromObj(lessonObj, "sectionend", 0);
                    int sectionstart = JsonUtils.getIntFromObj(lessonObj, "sectionstart", 0);

                    String locale = JsonUtils.getStringFromObj(lessonObj, "locale", "");
                    String name = JsonUtils.getStringFromObj(lessonObj, "name", "");
                    String period = JsonUtils.getStringFromObj(lessonObj, "smartPeriod", "");
                    String semester = JsonUtils.getStringFromObj(lessonObj, "semester", "");
                    String teacher = JsonUtils.getStringFromObj(lessonObj, "teacher", "");
                    String schoolName = JsonUtils.getStringFromObj(lessonObj, "schoolName", "");

                    lesson.setDay(day);
                    lesson.setLocale(locale);
                    lesson.setName(name);
                    lesson.setPeriod(period);
                    lesson.setSectionend(sectionend);
                    lesson.setSectionstart(sectionstart);
                    lesson.setSemester(semester);
                    lesson.setTeacher(teacher);
                    lesson.setSchoolName(schoolName);
                    lessons.add(lesson);
                }

                scanResult.setLessons(lessons);
                scanResult.setSuccess(true);
            }
            return scanResult;

        } catch (JSONException e) {
            e.printStackTrace();
            scanResult.setErrMsg("解析异常:" + result);
            scanResult.setSuccess(false);
            return scanResult;
        }
    }

    public static SuperResult parseScanResult(String result) {
        Log.d("TAG", "parseScanResult: " + result);
        SuperResult scanResult = new SuperResult();
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject data = obj.getJSONObject("data");
            if (data == null) {
                scanResult.setErrMsg("解析异常,不存在data字段");
                scanResult.setSuccess(false);
                return scanResult;
            }

            int status = JsonUtils.getIntFromObj(data, "statusInt", 1);
            if (status == 0) {
                scanResult.setErrMsg(JsonUtils.getStringFromObj(data, "errorStr", "未知错误"));
                scanResult.setSuccess(false);
            } else {
                JSONArray lessonList = data.getJSONArray("courseList");
                if (lessonList == null) {
                    scanResult.setErrMsg("解析异常,不存在courseList字段");
                    scanResult.setSuccess(false);
                    return scanResult;
                }

                List<SuperLesson> lessons = new ArrayList<>();
                for (int i = 0; i < lessonList.length(); i++) {
                    JSONObject lessonObj = lessonList.getJSONObject(i);
                    SuperLesson lesson = new SuperLesson();
                    int day = JsonUtils.getIntFromObj(lessonObj, "day", 0);
                    int sectionend = JsonUtils.getIntFromObj(lessonObj, "sectionEnd", 0);
                    int sectionstart = JsonUtils.getIntFromObj(lessonObj, "sectionStart", 0);

                    String locale = JsonUtils.getStringFromObj(lessonObj, "classroom", "");
                    String name = JsonUtils.getStringFromObj(lessonObj, "name", "");
                    String period = JsonUtils.getStringFromObj(lessonObj, "smartPeriod", "");
                    String semester = JsonUtils.getStringFromObj(lessonObj, "term", "");
                    String teacher = JsonUtils.getStringFromObj(lessonObj, "teacher", "");
                    String schoolName = JsonUtils.getStringFromObj(lessonObj, "schoolName", "");

                    lesson.setDay(day);
                    lesson.setLocale(locale);
                    lesson.setName(name);
                    lesson.setPeriod(period);
                    lesson.setSectionend(sectionend);
                    lesson.setSectionstart(sectionstart);
                    lesson.setSemester(semester);
                    lesson.setTeacher(teacher);
                    lesson.setSchoolName(schoolName);
                    lessons.add(lesson);
                }
                scanResult.setLessons(lessons);
                scanResult.setSuccess(true);

            }
            return scanResult;

        } catch (JSONException e) {
            e.printStackTrace();
            scanResult.setErrMsg("解析异常:" + result);
            scanResult.setSuccess(false);
            return scanResult;
        }
    }
}
