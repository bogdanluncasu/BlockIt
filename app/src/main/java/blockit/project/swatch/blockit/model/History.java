package blockit.project.swatch.blockit.model;

/**
 * Created by swatch on 12/17/16.
 */
public class History {
    Integer id;
    String sender,content,date;
    public History(Integer id, String sender, String content, String date){
        this.sender=sender;
        this.id=id;
        this.content=content;
        this.date=date;
    }

    public Integer getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
