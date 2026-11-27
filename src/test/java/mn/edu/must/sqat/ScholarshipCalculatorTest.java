package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ScholarshipCalculatorTest {

    private static final double EPS = 1e-9;

    ScholarshipCalculator calc;

    @BeforeEach
    void setUp() { calc = new ScholarshipCalculator(); }

    // ---- letterGrade: бүх салааг дайрдаг, гэхдээ юу ч нарийн шалгадаггүй ----

    @Test
    void letterGrade_exactGradeForAllRanges() {
        assertEquals("A", calc.letterGrade(95));
        assertEquals("B", calc.letterGrade(85));
        assertEquals("C", calc.letterGrade(75));
        assertEquals("D", calc.letterGrade(65));
        assertEquals("F", calc.letterGrade(30));
    }

    @Test
    void letterGrade_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    // ---- gpa: давталт, switch-ийн бүх салаа — гэхдээ зөвхөн муж шалгана ----

    @Test
    void gpa_exactValues() {
        assertEquals(3.5, calc.gpa(new double[]{95, 85}), EPS);
        assertEquals(1.5, calc.gpa(new double[]{75, 65}), EPS);
        assertEquals(0.0, calc.gpa(new double[]{40}), EPS);
    }

    @Test
    void gpa_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
    }

    // ---- scholarship: бүх дүрмийн салаа — гэхдээ зөвхөн муж шалгана ----

    @Test
    void scholarship_exactPercent() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
        assertEquals(70, calc.scholarship(3.5, 13, false));
        assertEquals(50, calc.scholarship(3.1, 5, false));
        assertEquals(0, calc.scholarship(2.0, 1, false));
        assertEquals(0, calc.scholarship(4.0, 20, true));
    }

    @Test
    void scholarship_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
    }

    // зааг утгын тестүүд

    @ParameterizedTest(name = "letterGrade({0}) = {1}")
    @CsvSource({
        "100,   A",
        "90,    A",
        "89.99, B",
        "80,    B",
        "79.99, C",
        "70,    C",
        "69.99, D",
        "60,    D",
        "59.99, F",
        "0,     F"
    })
    void letterGrade_boundaries(double score, String expected) {
        assertEquals(expected, calc.letterGrade(score));
    }

    @ParameterizedTest(name = "letterGrade({0}) throws")
    @ValueSource(doubles = {-0.01, 100.01})
    void letterGrade_justOutsideRange_throws(double score) {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(score));
    }

    @ParameterizedTest(name = "gpa([{0}]) = {1}")
    @CsvSource({
        "95, 4.0",
        "85, 3.0",
        "75, 2.0",
        "65, 1.0",
        "40, 0.0"
    })
    void gpa_eachLetterGrade(double score, double expected) {
        assertEquals(expected, calc.gpa(new double[]{score}), EPS);
    }

    @Test
    void gpa_manyScores_returnsAverage() {
        assertEquals(2.5, calc.gpa(new double[]{95, 85, 75, 65}), EPS);
    }

    @ParameterizedTest(name = "scholarship({0}, {1}, false) = {2}")
    @CsvSource({
        "3.8,  15, 100",
        "3.79, 15, 70",
        "3.8,  14, 70",
        "3.4,  12, 70",
        "3.39, 12, 50",
        "3.4,  11, 50",
        "3.0,   0, 50",
        "2.99,  0, 0",
        "4.0,  15, 100",
        "0.0,   0, 0"
    })
    void scholarship_boundaries(double gpa, int credits, int expected) {
        assertEquals(expected, calc.scholarship(gpa, credits, false));
    }

    @ParameterizedTest(name = "scholarship({0}, {1}, false) throws")
    @CsvSource({
        "-0.01, 10",
        "4.01,  10",
        "3.0,   -1"
    })
    void scholarship_outOfRange_throws(double gpa, int credits) {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(gpa, credits, false));
    }
}
