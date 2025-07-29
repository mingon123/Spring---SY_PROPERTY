package kr.spring.util;

public class StringUtil {
	//HTML을 허용하지 않으면서 줄바꿈
	public static String useBrNoHtml(String str){
		if(str == null) return null;

		return str.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;")
				.replaceAll("\r\n", "<br>")
				.replaceAll("\r", "<br>")
				.replaceAll("\n", "<br>");
	}
	//HTML을 허용하지 않음
	public static String useNoHtml(String str){
		if(str == null) return null;

		return str.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	/**
	 *일정 문자열이후에 ...으로 처리 
	 *
	 */
	public static String shortWords(int length, String content){

		if(content.length() > length){
			return content.substring(0,length) + " ...";
		}
		return content;
	}

	/* 동/읍/면/가 단위 이름 추출 */
	public static String extractRegionUnit(String address) {
		if (address == null) return "";

		// 공백 기준으로 split
		String[] parts = address.trim().split("\\s+");

		// 보통은 3~4번째 요소가 읍/면/동/가 단위
		for (int i = parts.length - 1; i >= 0; i--) {
			if (parts[i].endsWith("동") || parts[i].endsWith("읍") || parts[i].endsWith("면") || parts[i].endsWith("가")) {
				return parts[i];
			}
		}
		// 못 찾으면 마지막 요소
		return parts[parts.length - 1];
	}
	
	// 비밀번호 찾기 - 비밀번호 랜덤 생성
	public static String randomPassword(int length){
		int index = 0;
		char[] charSet = new char[]{
				'0','1','2','3','4','5','6','7','8','9'
				,'A','B','C','D','E','F','G','H','I','J','K','L','M'
				,'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
				,'a','b','c','d','e','f','g','h','i','j','k','l','m'
				,'n','o','p','q','r','s','t','u','v','w','x','y','z'
		};

		StringBuffer sb = new StringBuffer();

		for(int i=0;i<length;i++){
			index = (int)(charSet.length * Math.random());
			sb.append(charSet[index]);
		}

		return sb.toString();
	}
	
}
