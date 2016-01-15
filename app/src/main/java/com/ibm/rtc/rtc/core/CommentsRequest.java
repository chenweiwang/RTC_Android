package com.ibm.rtc.rtc.core;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.ibm.rtc.rtc.model.Comment;
import com.ibm.rtc.rtc.model.Workitem;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Mubai on 2016/1/14.
 */
public class CommentsRequest extends JsonRequest<List<Comment>> {

    public CommentsRequest(String url, Response.Listener<List<Comment>> listener,
                            Response.ErrorListener errorListener) {
        super(Method.GET, url, (String) null, listener, errorListener);
    }

    @Override
    protected Response<List<Comment>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonArrayString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            List<Comment> comments = Comment.fromJSONArray(jsonArray);
            return Response.success(comments, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        } catch (ParseException e) {
            return Response.error(new ParseError(e));
        }
    }

}
