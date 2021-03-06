package com.inflearn.restapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EventValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return EventDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventDto eventDto = (EventDto) target;

        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0){
            //rejectValue -> 필드 에러
            errors.rejectValue("basePrice","wrongValue","BasePrice is wrong.");
            errors.rejectValue("maxPrice","wrongValue","maxPrice is wrong.");

            //reject -> 글로벌 에러
            errors.reject("wrongPrices", "Values fo prices a Wrong");
        }

    }

}
