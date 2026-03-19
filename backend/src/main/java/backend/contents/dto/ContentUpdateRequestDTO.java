package backend.contents.dto;

import jakarta.validation.constraints.Size;

public class ContentUpdateRequestDTO {

    @Size(max = 100)
    private String title;

    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

