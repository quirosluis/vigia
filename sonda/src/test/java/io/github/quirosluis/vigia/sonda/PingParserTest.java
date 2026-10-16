package io.github.quirosluis.vigia.sonda;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PingParserTest {

    @Test
    void parseaPingOK() {
        PingResult p = PingParser.parsear("PING <ip-router> (<ip-router>) 56(84) bytes of data.\n" +
                "64 bytes from <ip-router>: icmp_seq=1 ttl=64 time=3.68 ms\n" +
                "\n" +
                "--- <ip-router> ping statistics ---\n" +
                "1 packets transmitted, 1 received, 0% packet loss, time 0ms\n" +
                "rtt min/avg/max/mdev = 3.687/3.687/3.687/0.000 ms\n" +
                "\n", 0);
        assertThat(p.latenciaMs()).isEqualTo(4);

    }

    @Test
    void redondeaAbajo() {
        PingResult p = PingParser.parsear("PING <ip-router> (<ip-router>) 56(84) bytes of data.\n" +
                "64 bytes from <ip-router>: icmp_seq=1 ttl=64 time=3.49 ms\n" +
                "\n" +
                "--- <ip-router> ping statistics ---\n" +
                "1 packets transmitted, 1 received, 0% packet loss, time 0ms\n" +
                "rtt min/avg/max/mdev = 3.687/3.687/3.687/0.000 ms\n" +
                "\n", 0);
        assertThat(p.latenciaMs()).isEqualTo(3);

    }

    @Test
    void parseaSinDecimales() {
        PingResult p = PingParser.parsear("PING <ip-router> (<ip-router>) 56(84) bytes of data.\n" +
                "64 bytes from <ip-router>: icmp_seq=1 ttl=64 time=2 ms\n" +
                "\n" +
                "--- <ip-router> ping statistics ---\n" +
                "1 packets transmitted, 1 received, 0% packet loss, time 0ms\n" +
                "rtt min/avg/max/mdev = 3.687/3.687/3.687/0.000 ms\n" +
                "\n", 0);
        assertThat(p.latenciaMs()).isEqualTo(2);

    }

    @Test
    void rechazaPingNull() {
        assertThatThrownBy(() -> PingParser.parsear(null, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ping");

    }

    @Test
    void rechazaCodigo0SinLatencia() {
        assertThatThrownBy(() -> PingParser.parsear("PING 192.0.2.1 (192.0.2.1) 56(84) bytes of data.\n" +
                "\n" +
                "--- 192.0.2.1 ping statistics ---\n" +
                "1 packets transmitted, 0 received, 100% packet loss, time 0ms\n" +
                "\n", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("latencia");
    }

    @Test
    void retornoOK0() {
        PingResult p = PingParser.parsear("PING <ip-router> (<ip-router>) 56(84) bytes of data.\n" +
                "64 bytes from <ip-router>: icmp_seq=1 ttl=64 time=3.49 ms\n" +
                "\n" +
                "--- <ip-router> ping statistics ---\n" +
                "1 packets transmitted, 1 received, 0% packet loss, time 0ms\n" +
                "rtt min/avg/max/mdev = 3.687/3.687/3.687/0.000 ms\n" +
                "\n", 0);
        assertThat(p).isEqualTo(new PingResult(true, 3));
    }

    @Test
    void sinRespuestaConCodigo1() {
        PingResult p = PingParser.parsear("PING 192.0.2.1 (192.0.2.1) 56(84) bytes of data.\n" +
                "\n" +
                "--- 192.0.2.1 ping statistics ---\n" +
                "1 packets transmitted, 0 received, 100% packet loss, time 0ms\n" +
                "\n", 1);
        assertThat(p).isEqualTo(new PingResult(false, null));
    }

    @Test
    void fallaDnsConCodigo2() {
        PingResult p = PingParser.parsear("ping: unknown host noexiste.invalid\n" +
                "\n", 2);
        assertThat(p).isEqualTo(new PingResult(false, null));
    }
}