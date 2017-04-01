package com.example.administrator.monitorcamera;

import com.google.android.mms.pdu.CharacterSets;
import com.google.android.mms.pdu.EncodedStringValue;
import com.google.android.mms.pdu.PduBody;
import com.google.android.mms.pdu.PduComposer;
import com.google.android.mms.pdu.PduPart;
import com.google.android.mms.pdu.SendReq;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class MMSInfo {
	private Context con;
	private PduBody pduBody;
	private String recieverNum;
	private int partCount = 1;

	private static final String SUBJECT_STR = "来自XX好友的彩信"; // 彩信主题

	public MMSInfo(Context con, String recieverNum) {
		// TODO Auto-generated constructor stub
		this.con = con;
		this.recieverNum = recieverNum;
		pduBody = new PduBody();
	}

	/**
	 * 添加图片附件，每添加一个附件调用本方法一次即可
	 * 
	 * @author
	 * @param uriStr
	 *            , 如：file://mnt/sdcard//1.jpg
	 */
	public void addPart(String uriStr) {
		PduPart part = new PduPart();
		part.setCharset(CharacterSets.UTF_8);
		part.setName(("附件" + partCount++).getBytes());
		// part.setContentType(("image/jpg" +
		// getTypeFromUri(uriStr)).getBytes());// "image/png"
		part.setContentType("image/jpeg".getBytes());
		part.setDataUri(Uri.parse(uriStr));
		pduBody.addPart(part);
	}
	
	/**
	 * 通过URI路径得到图片格式，如："file://mnt/sdcard//1.jpg" -----> "jpg"
	 * 
	 * @author
	 * @param uriStr
	 * @return
	 */
	private String getTypeFromUri(String uriStr) {
		return uriStr.substring(uriStr.lastIndexOf("."), uriStr.length());
	}

	/**
	 * 将彩信的内容以及主题等信息转化成byte数组，准备通过http协议发送到"http://mmsc.monternet.com"
	 * 
	 * @author
	 * @return
	 */
	public byte[] getMMSBytes() {
		PduComposer composer = new PduComposer(con, initSendReq());
		return composer.make();
	}

	/**
	 * 初始化SendReq
	 * 
	 * @author
	 * @return
	 */
	private SendReq initSendReq() {
		SendReq req = new SendReq();
		EncodedStringValue[] sub = EncodedStringValue.extract(SUBJECT_STR);
		if (sub != null && sub.length > 0) {
			req.setSubject(sub[0]);// 设置主题
		}
		EncodedStringValue[] rec = EncodedStringValue.extract(recieverNum);
		if (rec != null && rec.length > 0) {
			req.addTo(rec[0]);// 设置接收者
		}
		req.setBody(pduBody);
		return req;
	}
}
