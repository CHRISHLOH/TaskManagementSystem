package com.task.management.system.mapper;

import com.task.management.system.model.dto.CommentDto;
import com.task.management.system.model.dto.CreateCommentDto;
import com.task.management.system.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    List<CommentDto> listToDtoList(List<Comment> commentList);

    CommentDto commentToDto(Comment comment);

    Comment dtoToComment(CommentDto commentDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true) // если автор будет установлен отдельно
    @Mapping(target = "task", ignore = true)   // если задачу установим отдельно
    Comment createDtoToEntity(CreateCommentDto createCommentDto);
}
