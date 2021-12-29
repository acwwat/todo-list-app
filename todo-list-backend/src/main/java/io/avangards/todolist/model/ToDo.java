package io.avangards.todolist.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("todo")
public class ToDo {

	@Column("id")
	@Id
	private long id;

	@Column("summary")
	private String summary;

	@Column("desc")
	private String desc;

	@Column("createddt")
	private Date createdDt;

	@Column("completeddt")
	private Date completedDt;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	public Date getCompletedDt() {
		return completedDt;
	}

	public void setCompletedDt(Date completedDt) {
		this.completedDt = completedDt;
	}

}
