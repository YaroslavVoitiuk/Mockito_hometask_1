import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSender;

import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;

public class MainTest {

    @Test
    public void testMessageSender(){

        GeoService geoService = new GeoServiceImpl();
        LocalizationService localizationService = new LocalizationServiceImpl();
        Map<String,String> data = new HashMap<>();
        data.put("x-real-ip","96.");
        //172.0.32.11 96.44.183.149
        MessageSender messageSender = new MessageSenderImpl(geoService,localizationService);
        messageSender.send(data);

        String expectedRus = "Добро пожаловать";
        String expectedEn = "Welcome";
        assertThat(messageSender.send(data),anyOf(containsString(expectedEn),containsString(expectedRus)));

    }

    @Test
    public void testLocalizationService(){
        LocalizationService localizationService = new LocalizationServiceImpl();
        String expected = "Добро пожаловать";
        String actual = localizationService.locale(Country.RUSSIA);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void testGeoService(){
        GeoService geoService = new GeoServiceImpl();
        String expectedRus = "RUSSIA";
        String expectedUSA = "USA";
        assertThat(geoService.byIp("96.").getCountry().toString(),
                anyOf(containsString(expectedRus),containsString(expectedUSA)));

    }

    @Test
    public void testGeoServiceWithMockito(){
        GeoService geoService = Mockito.mock(GeoService.class);
        Mockito.when(geoService.byIp("96."))
                .thenReturn(new Location("Moscow", Country.RUSSIA, null, 0));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        assertThat(geoService.byIp("96.").getCountry().toString(),anyOf(containsString("RUSSIA")));
        Mockito.verify(geoService).byIp(argumentCaptor.capture());
        Assertions.assertEquals("96.",argumentCaptor.getValue());
    }

    @Test
    public void testLocalizationServiceWithSpy(){
        LocalizationService localizationService = Mockito.spy(LocalizationServiceImpl.class);
        assertThat(localizationService.locale(Country.USA),containsString("Welcome"));
    }


}

