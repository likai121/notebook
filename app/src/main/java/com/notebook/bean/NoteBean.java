package com.notebook.bean;

/**
 * @name: NoteBean
 * @date: 2020-05-18 10:29
 * @comment: 日记bean
 */
public class NoteBean {
    public int id;
    public String title;
    public String body;
    public long time;
    public int userId;

    @Override
    public String toString() {
        return "NoteBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", time=" + time +
                ", userId=" + userId +
                '}';
    }
}
