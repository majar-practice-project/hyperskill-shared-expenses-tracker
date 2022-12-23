package splitter.data;

import org.hibernate.annotations.SortNatural;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
public class SplitterGroup {
    @Id
    private String name;

    @SortNatural
    @ElementCollection(fetch = FetchType.EAGER)
    private SortedSet<String> members;

    public SplitterGroup(){}

    public SplitterGroup(String name, TreeSet<String> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortedSet<String> getMembers() {
        return members;
    }

    public void setMembers(TreeSet<String> members) {
        this.members = members;
    }
}
