package Designovel.Capstone.domain.variable.musinsaVariable;

import Designovel.Capstone.global.exception.CustomException;
import Designovel.Capstone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusinsaVariableService {
    private final MusinsaVariableRepository musinsaVariableRepository;

    /**
     * 무신사 스타일의 한섬 고유 변수 조회 메서드
     * @param styleId
     * @return 만약 해당 스타일의 고유 변수 존재하지 않을 시 400 에러 응답
     */
    public MusinsaVariable getMusinsaVariable(String styleId) {
        return musinsaVariableRepository.findByStyleId(styleId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.VARIABLE_NOT_FOUND_ID));
    }
}
