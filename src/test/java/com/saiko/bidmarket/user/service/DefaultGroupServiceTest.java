package com.saiko.bidmarket.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.repository.GroupRepository;

@ExtendWith(MockitoExtension.class)
class DefaultGroupServiceTest {

  @Mock
  GroupRepository groupRepository;

  @InjectMocks
  DefaultGroupService defaultGroupService;

  @Nested
  @DisplayName("findByName 메서드는")
  class DescribeFindByName {

    @Nested
    @DisplayName("name 값이 비어있으면")
    class ContextWithBlankName {

      @ParameterizedTest
      @NullAndEmptySource
      @ValueSource(strings = {"\n", "\t"})
      @DisplayName("IllegalArgumentException 에러를 발생시킨다.")
      void ItThrowsIllegalArgumentException(String src) {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> defaultGroupService.findByName(src)
        );
      }
    }

    @Nested
    @DisplayName("해당하는 group 이 존재 하면")
    class ContextWithExist {

      @Test
      @DisplayName("해당 group 객체를 반환한다")
      void ItResponseGroup() {
        //given
        String target = "USER_GROUP";

        Group group = new Group();
        ReflectionTestUtils.setField(group, "name", target);
        given(groupRepository.findByName(anyString())).willReturn(Optional.of(group));

        //when
        Group findGroup = defaultGroupService.findByName(target);

        //then
        assertEquals(target, findGroup.getName());
      }
    }

    @Nested
    @DisplayName("해당하는 group 이 존재하지 않으면")
    class ContextWithNotExist {

      @Test
      @DisplayName("NotfoundException 에러를 발생시킨다")
      void ItNotFoundException() {
        //given
        given(groupRepository.findByName(anyString())).willReturn(Optional.empty());

        //when, then
        assertThrows(NotFoundException.class, () -> defaultGroupService.findByName("USER_GROUP"));

      }
    }

  }

}
