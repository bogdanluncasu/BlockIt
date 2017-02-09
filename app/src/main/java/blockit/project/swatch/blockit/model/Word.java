package blockit.project.swatch.blockit.model;

import java.io.Serializable;

/**
 * Created by swatch on 12/17/16.
 */
public class Word implements Serializable {
    Integer id;
    String word;
    public Word(Integer id, String word){
        this.id=id;
        this.word=word;
    }

    public String getWord() {
        return word;
    }

    public Integer getId() {
        return id;
    }
}
