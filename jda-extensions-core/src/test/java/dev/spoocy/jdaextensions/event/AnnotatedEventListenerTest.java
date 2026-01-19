package dev.spoocy.jdaextensions.event;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.JDA;
import org.mockito.Mockito;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotatedEventListenerTest {

    private static final JDA MOCK_JDA = Mockito.mock(JDA.class);

     static class MyEvent extends Event {
         public final String payload;
        public MyEvent(String payload) { super(MOCK_JDA); this.payload = payload; }
     }

     static class Holder {
         final AtomicReference<String> last = new AtomicReference<>(null);

         @SubscribeEvent
         public void onMyEvent(MyEvent e) {
             last.set(e.payload);
         }

         @SubscribeEvent
         public void invalid() { /* wrong signature - should be ignored */ }
     }

     @Test
     public void invokesAnnotatedMethod() {
         Holder holder = new Holder();
         AnnotatedEventListener listener = new AnnotatedEventListener(holder);

         listener.onEvent(new MyEvent("hello"));

         assertEquals("hello", holder.last.get());
     }

     @Test
     public void equalsAndHashCodeForSameHolder() {
         Holder holder = new Holder();
         AnnotatedEventListener a = new AnnotatedEventListener(holder);
         AnnotatedEventListener b = new AnnotatedEventListener(holder);

         assertEquals(a, b);
         assertEquals(a.hashCode(), b.hashCode());

         AnnotatedEventListener c = new AnnotatedEventListener(new Holder());
         assertNotEquals(a, c);
     }
}
