package beans.controller;

import beans.models.Event;
import beans.models.Ticket;
import beans.models.User;
import beans.services.BookingService;
import beans.services.EventService;
import beans.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Controller
public class BookingController {
	@Autowired
	private BookingService bookingService;
	@Autowired
	private UserService userService;
	@Autowired
	private EventService eventService;

	@RequestMapping("/ticket/price/")
	public ModelAndView getTicketPrice(@RequestParam String event,
			@RequestParam String auditorium,
			@RequestParam String  localDateTime,
			@RequestParam List<Integer> seats,
			@RequestParam Long userId)  {

		User user = userService.getById(userId);
		double ticketPrice = bookingService.getTicketPrice(event, auditorium, LocalDateTime.parse(localDateTime), seats, user);
		ModelAndView modelAndView = new ModelAndView("ticket");
		modelAndView.addObject("price", ticketPrice);
		return modelAndView;
	}

	@RequestMapping("/ticket/book/")
	public ModelAndView bookTicket(@RequestParam long userId,
			@RequestParam String eventName,
			@RequestParam int seatNumber){
		User user = userService.getById(userId);
		Event event = eventService.getByName(eventName).get(0);
		double ticketPrice = bookingService.getTicketPrice(event.getName(), event.getAuditorium().getName(), event.getDateTime(), Collections.singletonList(seatNumber), user);
		Ticket ticket = new Ticket();
		ticket.setEvent(event);
		ticket.setDateTime(event.getDateTime());
		ticket.setUser(user);
		ticket.setPrice(ticketPrice);
		bookingService.bookTicket(user, ticket);

		ModelAndView modelAndView = new ModelAndView("ticketBooked");
		modelAndView.addObject("ticket", ticket);
		return modelAndView;
	}

	@RequestMapping("/{event}/tickets/")
	public void getTicketsForEvent(HttpServletResponse response, @PathVariable String event, @RequestParam String auditorium, @RequestParam String date) throws IOException {
		List<Ticket> tickets = bookingService.getTicketsForEvent(event, auditorium, LocalDateTime.parse(date));

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(tickets);
		byte[] data = bos.toByteArray();

		streamTickets(response, data, "my_report.pdf");
	}

	private void streamTickets(HttpServletResponse response, byte[] data, String name)
			throws IOException {

		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "attachment; filename=" + name);
		response.setContentLength(data.length);

		response.getOutputStream().write(data);
		response.getOutputStream().flush();
	}

}
