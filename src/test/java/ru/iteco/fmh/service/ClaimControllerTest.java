package ru.iteco.fmh.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteco.fmh.controller.ClaimController;
import ru.iteco.fmh.dao.repository.ClaimRepository;
import ru.iteco.fmh.dao.repository.UserRepository;
import ru.iteco.fmh.dto.claim.ClaimDto;
import ru.iteco.fmh.dto.user.UserDto;
import ru.iteco.fmh.dto.wish.WishShortInfoDto;
import ru.iteco.fmh.model.task.StatusE;
import ru.iteco.fmh.model.task.claim.Claim;
import ru.iteco.fmh.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import static ru.iteco.fmh.TestUtils.*;
import static ru.iteco.fmh.converter.ClaimDtoToClaimConverterTest.getClaimDto;

// ТЕСТЫ ЗАВЯЗАНЫ НА ТЕСТОВЫЕ ДАННЫЕ В БД!!
@RunWith(SpringRunner.class)
@SpringBootTest()
public class ClaimControllerTest {
    @Autowired
    ClaimController sut;
    @Autowired
    ClaimRepository claimRepository;
    @Autowired
    ConversionServiceFactoryBean factoryBean;
    @Autowired
    UserRepository userRepository;


    @Test
    public void getAllOpenAndInProgressClaims() {
        List<ClaimDto> claimDtoList = sut.getAllClaims();
        for (ClaimDto claimDto : claimDtoList) {
            System.out.println(claimDto);
        }
        assertEquals(4, claimDtoList.size());
        assertTrue(claimDtoList.get(1).getPlanExecuteDate().isBefore
                (claimDtoList.get(2).getPlanExecuteDate()));
    }

    @Test
    public void createClaimShouldPassSuccess() {
        ConversionService conversionService = factoryBean.getObject();

        // given
        ClaimDto given = getClaimDto();
        //executor null
        given.setCreator(conversionService.convert(userRepository.findUserById(1), UserDto.class));
        Integer id = sut.createClaim(given);
        assertNotNull(id);


        Claim result = claimRepository.findById(id).get();
        assertEquals(result.getStatus(), StatusE.OPEN);

        assertAll(
                () -> assertEquals(given.getDescription(), result.getDescription()),
                () -> assertEquals(given.getTitle(), result.getTitle()),
                () -> assertEquals(given.getCreator(), conversionService.convert(result.getCreator(), UserDto.class)),
                () -> assertNull(result.getExecutor()),
                () -> assertEquals(given.getCreateDate(), result.getCreateDate()),
                () -> assertEquals(given.getPlanExecuteDate(), result.getPlanExecuteDate()),
                () -> assertEquals(given.getFactExecuteDate(), result.getFactExecuteDate()),
                () -> assertEquals(given.getStatus(), result.getStatus())
        );
        //executor notNull
        given.setExecutor(conversionService.convert(userRepository.findUserById(1), UserDto.class));
        Integer idNotNullExecutor = sut.createClaim(given);
        assertNotNull(idNotNullExecutor);
        Claim resultNotNullExecutor = claimRepository.findById(idNotNullExecutor).get();
        assertEquals(resultNotNullExecutor.getStatus(), StatusE.IN_PROGRESS);

        // deleting result entity
        claimRepository.deleteById(id);
        claimRepository.deleteById(idNotNullExecutor);
    }


//
//    @Test
//    public void getClaimShouldPassSuccess() {
//        ConversionService conversionService = factoryBean.getObject();
//        int claimId=1;
//        ClaimDto expected = conversionService.convert(claimRepository.findById(claimId).get(), ClaimDto.class);
//        ClaimDto result = sut.getClaim(claimId);
//        assertAll(
//                ()-> assertEquals(expected.getDescription(), result.getDescription()),
//                ()-> assertEquals(expected.getCreator(), result.getCreator()),
//                ()-> assertEquals(expected.getExecutor(), result.getExecutor()),
//                ()-> assertEquals(expected.getStatus(), result.getStatus()),
//                ()-> assertEquals(expected.getCreateDate(), result.getCreateDate()),
//                ()-> assertEquals(expected.getFactExecuteDate(), result.getFactExecuteDate()),
//                ()-> assertEquals(expected.getPlanExecuteDate(), result.getPlanExecuteDate()),
//                ()-> assertEquals(expected.getId(), result.getId())
//        );
//    }

//    @Test
//    public void getAllActiveNotesSort() {
//        List<ClaimShortInfoDto> claimShortInfoDtoList = sut.getAllClaims();
//        assertEquals(4, claimShortInfoDtoList.size());
//        assertTrue(claimShortInfoDtoList.get(1).getPlanExecuteDate().isBefore
//                (claimShortInfoDtoList.get(2).getPlanExecuteDate()));
//        }

//    @Test
//    public void updateClaimShouldPassSuccess() {
//        ConversionService conversionService = factoryBean.getObject();
//
//        // given
//        int claimId = 3;
//        ClaimDto given = conversionService.convert(claimRepository.findById(claimId).get(), ClaimDto.class);
//        String newComment = "new comment";
//
//        assertNotEquals(given.getComment(), newComment);
//
//        given.setComment(newComment);
//
//        ClaimDto result = sut.updateClaim(given);
//
//        assertEquals(given.getComment(), result.getComment());
//    }

//    @Test
//    public void changeStatusShouldPassSuccess() {
//        int claimId = 4;
//        int claimId2 = 5;
//        ClaimDto resultCancelled = sut.changeStatus(claimId, EXECUTED);
//        ClaimDto resultCancelled2 = sut.changeStatus(claimId2, CANCELED);
//        assertEquals(EXECUTED, resultCancelled.getStatus());
//        assertEquals(LocalDateTime.now().withNano(0),resultCancelled.getFactExecuteDate());
//        assertEquals(CANCELED, resultCancelled2.getStatus());
//        assertNull(resultCancelled2.getFactExecuteDate());
//        Claim claim = claimRepository.findById(4).get();
//        claim.setStatus(OPEN);
//        claimRepository.save(claim);
//    }
//    @Test
//    public void changeStatusNotShouldPassSuccessWrongId() {
//        int claimId = 12;
//        assertThrows(IllegalArgumentException.class, () -> sut.changeStatus(claimId, EXECUTED));
//        assertThrows(IllegalArgumentException.class, () -> sut.changeStatus(5, OPEN));
//    }

}
