package com.jjcompany.jjcinemabackend.domain;

import java.util.ArrayList;
import java.util.List;

public final class SeatLayout {

    private static final List<Character> ROWS = List.of('A', 'B', 'C', 'D', 'E'); //불변 리스트 생성
    private static final int COLUMNS = 8;
    private static final List<String> ALL_SEAT_CODES;

    static {
        List<String> seats = new ArrayList<>();

        for (char row : ROWS) {              // 행 하나씩 꺼내서 (A, B, C, ...)
            for (int col = 1; col <= COLUMNS; col++) {   // 그 행 안에서 1~8까지 반복
                seats.add(row + String.valueOf(col));    // "A1", "A2", ... 이어붙여서 추가
            }
        }

        ALL_SEAT_CODES = seats;
    }

    private SeatLayout(){}

    public static List<String> allSeatCodes(){
        return ALL_SEAT_CODES;
    }

    public static boolean isValid(String seatCode){
        return ALL_SEAT_CODES.contains(seatCode);
    }
}
