package com.task.management.system.mapper;

import com.task.management.system.enums.Priority;
import com.task.management.system.enums.Status;
import com.task.management.system.model.dto.TaskDto;
import com.task.management.system.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusFromDto")
    @Mapping(target = "priority", source = "priority", qualifiedByName = "priorityFromDto")
    Task toEntity(TaskDto taskDto);

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "status", source = "status.displayName")
    @Mapping(target = "priority", source = "priority.displayName")
    TaskDto toDto(Task task);

    List<TaskDto> listToDtoList(List<Task> taskList);

    @Named("statusFromDto")
    default Status statusFromDto(String status) {
        return Status.fromDisplayName(status);
    }

    @Named("priorityFromDto")
    default Priority priorityFromDto(String priority) {
        return Priority.fromDisplayName(priority);
    }
}
