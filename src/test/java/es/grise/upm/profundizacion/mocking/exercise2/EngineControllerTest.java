package es.grise.upm.profundizacion.mocking.exercise2;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class EngineControllerTest {

    private EngineController engineController;
    private Logger loggerMock;
    private Speedometer speedometerMock;
    private Gearbox gearboxMock;
    private Time timeMock;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        // Crear los mocks de las dependencias
        this.loggerMock = mock(Logger.class);
        this.speedometerMock = mock(Speedometer.class);
        this.gearboxMock = mock(Gearbox.class);
        this.timeMock = mock(Time.class);

        // Inyectar las dependencias mockeadas en EngineController
        this.engineController = new EngineController(this.loggerMock, this.speedometerMock, this.gearboxMock, this.timeMock);
    }
    @Test
    void testLogFormat() {
        // Configuramos el mock de Time para devolver una hora específica
        Timestamp fixedTime = Timestamp.valueOf("2024-11-24 12:00:00");
        when(timeMock.getCurrentTime()).thenReturn(fixedTime);

        // Configuramos el mock de Speedometer para que devuelva una velocidad simulada
        when(speedometerMock.getSpeed()).thenReturn(10.0);

        // Llamamos al método que debe generar el log
        engineController.recordGear(GearValues.FIRST);

        // Verificamos que el log tiene el formato correcto
        String expectedLogMessage = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fixedTime) + " Gear changed to " + GearValues.FIRST;
        verify(loggerMock).log(expectedLogMessage); // Verifica que se haya invocado con el mensaje correcto
    }

    @Test
    void testInstantaneousSpeed() {
        // Configuramos los mocks para que devuelvan una velocidad simulada
        when(speedometerMock.getSpeed()).thenReturn(20.0).thenReturn(30.0).thenReturn(40.0);

        // Llamamos al método que calcula la velocidad
        double averageSpeed = engineController.getInstantaneousSpeed();

        assertEquals(30.0, averageSpeed, 0.01);
    }

    @Test
    void testAdjustGearCalls() {
        // Configuramos el mock de Speedometer para devolver una velocidad simulada
        when(speedometerMock.getSpeed()).thenReturn(50.0).thenReturn(60.0).thenReturn(70.0);

        // Configuramos el mock de Time para devolver una hora específica
        Timestamp fixedTime = Timestamp.valueOf("2024-11-24 12:00:00");
        when(timeMock.getCurrentTime()).thenReturn(fixedTime);

        // Llamamos al método adjustGear
        engineController.adjustGear();

        // Verificamos que el método getSpeed() fue llamado tres veces
        verify(speedometerMock, times(3)).getSpeed();
    }

    @Test
    void testAdjustGearLog() {
        // Configuramos el mock de Speedometer para devolver una velocidad simulada
        when(speedometerMock.getSpeed()).thenReturn(50.0).thenReturn(60.0).thenReturn(70.0);

        // Configuramos el mock de Time para devolver una hora específica
        Timestamp fixedTime = Timestamp.valueOf("2024-11-24 12:00:00");
        when(timeMock.getCurrentTime()).thenReturn(fixedTime);

        // Llamamos al método adjustGear
        engineController.adjustGear();

        // Capturamos el argumento pasado al logger
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(loggerMock).log(captor.capture());

        // Verificamos que el log contiene el mensaje esperado
        String logMessage = captor.getValue();
        assertTrue(logMessage.contains("Gear changed to"));
    }

    @Test
    void testAdjustGearSet() {
        // Configura el mock de Speedometer para devolver una velocidad suficientemente alta
        when(speedometerMock.getSpeed()).thenReturn(18.0).thenReturn(18.0).thenReturn(18.0);

        // Configura el mock de Time para devolver un valor de tiempo
        Timestamp fixedTime = Timestamp.valueOf("2024-11-24 12:00:00");
        when(timeMock.getCurrentTime()).thenReturn(fixedTime);

        // Llama al método adjustGear
        engineController.adjustGear();

        // Captura el argumento pasado al método setGear
        ArgumentCaptor<GearValues> captor = ArgumentCaptor.forClass(GearValues.class);
        verify(gearboxMock).setGear(captor.capture());

        // Verifica que el valor capturado es el correcto
        assertEquals(GearValues.FIRST,captor.getValue());
    }

}
