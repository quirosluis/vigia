package io.github.quirosluis.vigia.sonda;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BateriaParserTest {

    @Test
    void interpretaBateriaEnchufadaNoCargaPorFull() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "plugged": "PLUGGED_AC",
                  "status": "FULL",
                  "temperature": 26.9,
                  "voltage": 3999,
                  "current": -2306000,
                  "percentage": 22,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 909668
                }
                """;
        assertThat(BateriaParser.parsear(json)).isEqualTo(new EstadoBateria(22, true));
    }

    @Test
    void interpretaBateriaCargando() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "plugged": "PLUGGED_AC",
                  "status": "CHARGING",
                  "temperature": 26.9,
                  "voltage": 3999,
                  "current": -2306000,
                  "percentage": 22,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 909668
                }
                """;
        assertThat(BateriaParser.parsear(json)).isEqualTo(new EstadoBateria(22, true));
    }

    @Test
    void interpretaBateriaCargandoUSB() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "plugged": "PLUGGED_USB",
                  "status": "CHARGING",
                  "temperature": 26.9,
                  "voltage": 3999,
                  "current": -2306000,
                  "percentage": 22,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 909668
                }
                """;
        assertThat(BateriaParser.parsear(json)).isEqualTo(new EstadoBateria(22, true));
    }

    @Test
    void interpretaBateriaCargandoWIRELESS() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "plugged": "PLUGGED_WIRELESS",
                  "status": "CHARGING",
                  "temperature": 26.9,
                  "voltage": 3999,
                  "current": -2306000,
                  "percentage": 22,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 909668
                }
                """;
        assertThat(BateriaParser.parsear(json)).isEqualTo(new EstadoBateria(22, true));
    }

    @Test
    void interpretaBateriaDescargando() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "plugged": "UNPLUGGED",
                  "status": "DISCHARGING",
                  "temperature": 26.8,
                  "voltage": 3736,
                  "current": 144000,
                  "percentage": 22,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 913107
                }
                """;
        assertThat(BateriaParser.parsear(json)).isEqualTo(new EstadoBateria(22, false));
    }

    @Test
    void interpretaPluggedDesconocido() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "plugged": "PATATA",
                  "status": "DISCHARGING",
                  "temperature": 26.8,
                  "voltage": 3736,
                  "current": 144000,
                  "percentage": 22,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 913107
                }
                """;
        assertThatThrownBy(() -> BateriaParser.parsear(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("desconocido");
    }

    @Test
    void interpretaPercentageAusente() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "plugged": "UNPLUGGED",
                  "status": "DISCHARGING",
                  "temperature": 26.8,
                  "voltage": 3736,
                  "current": 144000,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 913107
                }
                """;
        assertThatThrownBy(() -> BateriaParser.parsear(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Falta el campo");
    }

    @Test
    void interpretaPluuggedAusente() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "status": "DISCHARGING",
                  "temperature": 26.8,
                  "voltage": 3736,
                  "current": 144000,
                  "percentage": 22,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 913107
                }
                """;
        assertThatThrownBy(() -> BateriaParser.parsear(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Falta el campo");
    }

    @Test
    void interpretaPercentageNull() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "plugged": "UNPLUGGED",
                  "status": "DISCHARGING",
                  "temperature": 26.8,
                  "voltage": 3736,
                  "current": 144000,
                  "percentage": null,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 913107
                }
                """;
        assertThatThrownBy(() -> BateriaParser.parsear(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Falta el campo");
    }

    @Test
    void interpretaPercentageInferiorMin() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "plugged": "UNPLUGGED",
                  "status": "DISCHARGING",
                  "temperature": 26.8,
                  "voltage": 3736,
                  "current": 144000,
                  "percentage": -1,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 913107
                }
                """;
        assertThatThrownBy(() -> BateriaParser.parsear(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fuera de rango");
    }

    @Test
    void interpretaPercentageSuperiorMax() {
        String json = """
                {
                  "present": true,
                  "technology": "Li-ion",
                  "health": "GOOD",
                  "plugged": "UNPLUGGED",
                  "status": "DISCHARGING",
                  "temperature": 26.8,
                  "voltage": 3736,
                  "current": 144000,
                  "percentage": 101,
                  "level": 22,
                  "scale": 100,
                  "charge_counter": 913107
                }
                """;
        assertThatThrownBy(() -> BateriaParser.parsear(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fuera de rango");
    }

    @Test
    void rechazaJsonRoto() {
        assertThatThrownBy(() -> BateriaParser.parsear("{ \"percentage\": 30, "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasCauseInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class);
    }
}
