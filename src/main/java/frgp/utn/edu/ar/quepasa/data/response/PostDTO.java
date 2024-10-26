package frgp.utn.edu.ar.quepasa.data.response;

import java.sql.Timestamp;

public class PostDTO {
    private Integer id;
    private boolean active;
    private String audience;
    private String description;
    private String tags;
    private Timestamp timestamp;
    private String title;
    private Integer neighbourhood;
    private Integer op;
    private Integer subtype;
    private String postTypeDescription;
    private String postSubtypeDescription;
    private Integer totalVotes;
    private Integer userVotes;
    private Integer commentId;
    private Integer pictureId;
    private String pictureDescription;
    private Timestamp pictureUploadedAt;
    private String pictureMediaType;
    private Integer score;

    public Integer getId() {
      return this.id;
    }
    public void setId(Integer value) {
      this.id = value;
    }

    public boolean getActive() {
      return this.active;
    }
    public void setActive(boolean value) {
      this.active = value;
    }

    public String getAudience() {
      return this.audience;
    }
    public void setAudience(String value) {
      this.audience = value;
    }

    public String getDescription() {
      return this.description;
    }
    public void setDescription(String value) {
      this.description = value;
    }

    public String getTags() {
      return this.tags;
    }
    public void setTags(String value) {
      this.tags = value;
    }

    public Timestamp getTimestamp() {
      return this.timestamp;
    }
    public void setTimestamp(Timestamp value) {
      this.timestamp = value;
    }

    public String getTitle() {
      return this.title;
    }
    public void setTitle(String value) {
      this.title = value;
    }

    public Integer getNeighbourhood() {
      return this.neighbourhood;
    }
    public void setNeighbourhood(Integer value) {
      this.neighbourhood = value;
    }

    public Integer getOp() {
      return this.op;
    }
    public void setOp(Integer value) {
      this.op = value;
    }

    public Integer getSubtype() {
      return this.subtype;
    }
    public void setSubtype(Integer value) {
      this.subtype = value;
    }

    public String getPostTypeDescription() {
      return this.postTypeDescription;
    }
    public void setPostTypeDescription(String value) {
      this.postTypeDescription = value;
    }

    public String getPostSubtypeDescription() {
      return this.postSubtypeDescription;
    }
    public void setPostSubtypeDescription(String value) {
      this.postSubtypeDescription = value;
    }

    public Integer getTotalVotes() {
      return this.totalVotes;
    }
    public void setTotalVotes(Integer value) {
      this.totalVotes = value;
    }

    public Integer getUserVotes() {
      return this.userVotes;
    }
    public void setUserVotes(Integer value) {
      this.userVotes = value;
    }

    public Integer getCommentId() {
      return this.commentId;
    }
    public void setCommentId(Integer value) {
      this.commentId = value;
    }

    public Integer getPictureId() {
      return this.pictureId;
    }
    public void setPictureId(Integer value) {
      this.pictureId = value;
    }

    public String getPictureDescription() {
      return this.pictureDescription;
    }
    public void setPictureDescription(String value) {
      this.pictureDescription = value;
    }

    public Timestamp getPictureUploadedAt() {
      return this.pictureUploadedAt;
    }
    public void setPictureUploadedAt(Timestamp value) {
      this.pictureUploadedAt = value;
    }

    public String getPictureMediaType() {
      return this.pictureMediaType;
    }
    public void setPictureMediaType(String value) {
      this.pictureMediaType = value;
    }

    public Integer getScore() {
      return this.score;
    }
    public void setScore(Integer value) {
      this.score = value;
    }
}

