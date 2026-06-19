package com.comdao.api.notices.entities;

import com.comdao.api.notices.entities.enums.Type;
import com.comdao.api.user.entities.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(
        name = "notices"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_seq_gen")
    @SequenceGenerator(name = "notice_seq_gen", allocationSize = 1, sequenceName = "notice_seq")
    private Long id;
    private Type type;
    //    private Boolean read = false;
    private String title;
    private String summary;
    private String content;
    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy")
    private Date created = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
