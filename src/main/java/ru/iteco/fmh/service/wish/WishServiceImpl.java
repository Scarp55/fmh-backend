package ru.iteco.fmh.service.wish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.fmh.dao.repository.WishRepository;
import ru.iteco.fmh.dto.wish.WishDto;
import ru.iteco.fmh.model.task.wish.Wish;
import ru.iteco.fmh.model.task.StatusE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.iteco.fmh.model.task.StatusE.*;

@Service
public class WishServiceImpl implements WishService {

    private final WishRepository wishRepository;
    private final ConversionServiceFactoryBean factoryBean;

    @Autowired
    public WishServiceImpl(WishRepository wishRepository, ConversionServiceFactoryBean factoryBean) {
        this.wishRepository = wishRepository;
        this.factoryBean = factoryBean;
    }

    @Override
    public List<WishDto> getAllWishes() {
        List<Wish> list = wishRepository.findAllByStatusInOrderByPlanExecuteDateAscCreateDateAsc(List.of(OPEN, IN_PROGRESS));
        ConversionService conversionService = factoryBean.getObject();
        return list.stream()
                .map(i -> conversionService.convert(i, WishDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Integer createWish(WishDto wishDto) {
        wishDto.setStatus(wishDto.getExecutor() == null ? OPEN : IN_PROGRESS);
        Wish wish = factoryBean.getObject().convert(wishDto, Wish.class);
        return wishRepository.save(wish).getId();
    }

    @Override
    public WishDto getWish(Integer id) {
        Wish wish = wishRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Просьбы с таким ID не существует"));
        ConversionService conversionService = factoryBean.getObject();
        return conversionService.convert(wish, WishDto.class);
    }

    @Transactional
    @Override
    public WishDto updateWish(WishDto wishDto) {
        ConversionService conversionService = factoryBean.getObject();
        Wish wish = conversionService.convert(wishDto, Wish.class);
        if (OPEN.equals(wish.getStatus())) {
            wish = wishRepository.save(wish);
            return conversionService.convert(wish, WishDto.class);
        } else {
            throw new IllegalArgumentException("невозможно изменить записку с данным статусом");
        }
    }

    @Override
    public List<WishDto> getPatientWishes(Integer patientId) {
        ConversionService conversionService = factoryBean.getObject();
        return wishRepository.findAllByPatient_IdAndDeletedIsFalseAndStatus(patientId, OPEN).stream()
                .map(note -> conversionService.convert(note, WishDto.class))
                .collect(Collectors.toList());
    }
//нет больше комента
//    @Transactional
//    @Override
//    public WishDto addComment(Integer noteId, String comment) {
//        Optional<Wish> optionalNote = wishRepository.findById(noteId);
//
//        if (optionalNote.isPresent()) {
//            Wish wish = optionalNote.get();
//            if (!wish.getComment().isEmpty()) {
//                wish.setComment(wish.getComment().concat(", ").concat(comment));
//            } else {
//                wish.setComment(comment);
//            }
//            wish = wishRepository.save(wish);
//            ConversionService conversionService = factoryBean.getObject();
//            return conversionService.convert(wish, WishDto.class);
//        } else {
//            throw new IllegalArgumentException("записка не найдена!");
//        }
//    }

    @Transactional
    @Override
    public WishDto changeStatus(Integer wishId, StatusE status) {
        Optional<Wish> optionalNote = wishRepository.findById(wishId);
        if (optionalNote.isPresent()) {
            ConversionService conversionService = factoryBean.getObject();
            Wish wish = optionalNote.get();
            wish.changeStatus(status);
            wish = wishRepository.save(wish);
            return conversionService.convert(wish, WishDto.class);
        }
        throw new IllegalArgumentException("Просьбы с таким ID не существует");
    }
}
