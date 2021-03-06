package com.spartanapps.ibeaconsocializer;

import java.util.Comparator;

public class ConversationItem implements Comparable<ConversationItem> {

	private String innerUser1;
	private String innerUser2;
	private String innerStatus;
	private String innerMessage;
	private long innerLastMessageDate;

	public ConversationItem(String user1, String user2, String status,
			String message, long lastmessage) {

		this.innerUser1 = user1;
		this.innerUser2 = user2;
		this.innerStatus = status;
		this.innerMessage = message;
		this.innerLastMessageDate = lastmessage;

	}

	public String getInnerUser1() {
		return innerUser1;
	}

	public void setInnerUser1(String innerUser1) {
		this.innerUser1 = innerUser1;
	}

	public String getInnerUser2() {
		return innerUser2;
	}

	public void setInnerUser2(String innerUser2) {
		this.innerUser2 = innerUser2;
	}

	public String getInnerStatus() {
		return innerStatus;
	}

	public void setInnerStatus(String innerStatus) {
		this.innerStatus = innerStatus;
	}

	public String getInnerMessage() {
		return innerMessage;
	}

	public void setInnerMessage(String innerMessage) {
		this.innerMessage = innerMessage;
	}

	public long getInnerLastMessageDate() {

		return innerLastMessageDate;
	}

	public void setInnerLastMessageDate(int innerLastMessageDate) {
		this.innerLastMessageDate = innerLastMessageDate;
	}

	public static Comparator<ConversationItem> SORT_TEAMS = new Comparator<ConversationItem>() {
		@Override
		public int compare(ConversationItem o1, ConversationItem o2) {
			if (o1.getInnerLastMessageDate() > o2.getInnerLastMessageDate())
				return -1;
			else
				return 1;
		}
	};

	@Override
	public int compareTo(ConversationItem arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
