package com.ibm.rtc.rtc.core;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.ibm.rtc.rtc.model.Workitem;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Jack on 2015/12/23.
 */
public class WorkitemsRequest extends JsonRequest<List<Workitem>> {

    public WorkitemsRequest(String url, Response.Listener<List<Workitem>> listener,
                            Response.ErrorListener errorListener) {
        super(Method.GET, url, (String) null, listener, errorListener);
    }

    @Override
    protected Response<List<Workitem>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonArrayString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            List<Workitem> workitems = Workitem.fromJSONArray(jsonArray);
            return Response.success(workitems, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        } catch (ParseException e) {
            return Response.error(new ParseError(e));
        }
    }

}
