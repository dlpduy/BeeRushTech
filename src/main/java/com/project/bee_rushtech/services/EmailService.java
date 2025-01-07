package com.project.bee_rushtech.services;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.bee_rushtech.models.Email;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.OrderDetail;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.utils.SecurityUtil;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;

    public void sendEmail(Email email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("beerushtech@gmail.com"); // tài khoản gửi mail
            helper.setTo(email.getToEmail()); // tài khoản nhận mail
            helper.setSubject(MimeUtility.encodeText(email.getSubject(), "UTF-8", "B")); // tiêu đề mail
            helper.setText(email.getBody(), true); // nội dung mail
            message.setContent(email.getBody(), "text/html; charset=UTF-8");

            javaMailSender.send(message); // gửi mail
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSendMailShip(Order order) throws Exception {
        Long userId = order.getUser().getId();
        User user = userService.findById(userId);
        String toEmail = user.getEmail();
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());
        Float collateral = 0f;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail detail = orderDetails.get(i);

            Long days = detail.getTimeRenting() / 24;
            Long hours = detail.getTimeRenting() % 24;
            if (detail.getProduct().getImportPrice() < 10000000) {
                collateral += 1000000;
            } else if (detail.getProduct().getImportPrice() < 20000000) {
                collateral += 2000000;
            } else {
                collateral += 3000000;
            }

            // Định dạng chuỗi kết quả
            String timeRenting = String.format("%d Days, %d hours", days, hours);

            result.append(String.format("<p> %d. Product: %s - Quantity: %d - Time Renting: %s </p>",
                    i + 1,
                    detail.getProduct().getName(),
                    detail.getNumberOfProducts(),
                    timeRenting));
        }
        String subject = "[BeeRushTech] Xác Nhận Đơn Hàng";
        String body = "<html>"
                + "<body>"
                + "<p style='font-weight: bold;'>Kính gửi " + user.getFullName() + ",</p>"
                + "<p>Cảm ơn bạn đã tin tưởng và sử dụng dịch vụ của chúng tôi. Đơn hàng của bạn đã được xác nhận và đang được chuẩn bị. Bạn có thể xem lại thông tin đơn hàng bên dưới:</p>"
                + "<p><strong>THÔNG TIN VỀ ĐƠN HÀNG " + order.getId() + "</strong></p>"
                + "<p><strong>Sản phẩm thuê: " + "</strong></p>"
                + "<p>" + result.toString() + "</p>"
                + "<p><strong>Tổng giá trị: </strong>" + order.getTotalMoney().longValue() + " VND</p>"
                + "<p><strong>Phương thức thanh toán: </strong>" + order.getPaymentMethod() + "</p>"
                + "<p><strong>Mã vận chuyển: </strong>" + order.getTrackingNumber() + "</p>"
                + "<p><strong>Địa chỉ nhận hàng: </strong>" + order.getAddress() + "</p>"
                + "<br>"
                + "<p>Để thay đổi bất kỳ thông tin nào, vui lòng liên hệ với chúng tôi ít nhất 24 giờ trước khi giao hàng.</p>"
                + "<p>Vì giá trị sản phẩm của bạn khá cao, vui lòng chuẩn bị khoản tiền thế chấp là "
                + collateral.longValue() + " VND khi nhận hàng từ nhân viên giao hàng.</p>"
                + "<p>Trân trọng,<br> Bộ phận chăm sóc khách hàng Bee RushTech</p>"
                + "<br>"
                + "<p><strong>Lưu ý:</strong></p>"
                + "<ul>"
                + "<li style='color: red;'>Trước khi nhận gói hàng, vui lòng kiểm tra để đảm bảo rằng gói hàng không bị hư hỏng hoặc bị mở trước khi đến tay bạn. Nếu gói hàng bị hư hỏng và bạn nghi ngờ rằng gói hàng đã bị mở trước, hãy tạo phiếu khiếu nại với đơn vị vận chuyển. Chỉ bằng cách này, chúng tôi mới có thể xác định bên chịu trách nhiệm và nếu cần, hoàn tiền hoặc sắp xếp lại lô hàng khác.</li>"
                + "<br>"
                + "<li style='color: red;'>Trong thời gian thuê, nếu có bất kỳ hư hại nào xảy ra đối với thiết bị, người thuê sẽ chịu trách nhiệm toàn bộ chi phí sửa chữa. Trong trường hợp thiết bị không thể sửa chữa, người thuê phải bồi thường giá trị mua mới của thiết bị. Ngoài ra, người thuê cần bồi thường cho bất kỳ thiệt hại kinh tế nào do thiết bị không thể được cho thuê.</li>"
                + "</ul>"
                + "<br>"
                + "<p>Vui lòng liên hệ với chúng tôi qua các cách sau:</p>"
                + "<p>Email: hien.nguyenhophuoc@hcmut.edu.vn</p>"
                + "<p>Điện thoại: 0869018053</p>"
                + "<p>Showroom: 268, Lý Thường Kiệt, Phường 14, Quận 10, TP.HCM.</p>"
                + "</body>"
                + "</html>";

        Email email = new Email();
        email.setToEmail(toEmail);
        email.setSubject(subject);
        email.setBody(body);
        sendEmail(email);
    }

    public void handleSendMailStore(Order order) throws Exception {
        Long userId = order.getUser().getId();
        User user = userService.findById(userId);
        String toEmail = user.getEmail();
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());
        Float collateral = 0f;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail detail = orderDetails.get(i);

            Long days = detail.getTimeRenting() / 24;
            Long hours = detail.getTimeRenting() % 24;
            if (detail.getProduct().getImportPrice() < 10000000) {
                collateral += 1000000;
            } else if (detail.getProduct().getImportPrice() < 20000000) {
                collateral += 2000000;
            } else {
                collateral += 3000000;
            }

            // Định dạng chuỗi kết quả
            String timeRenting = String.format("%d Days, %d hours", days, hours);

            result.append(String.format("<p> %d. Product: %s - Quantity: %d - Time Renting: %s </p>",
                    i + 1,
                    detail.getProduct().getName(),
                    detail.getNumberOfProducts(),
                    timeRenting));
        }

        String subject = "[BeeRushTech] Xác Nhận Đơn Hàng";
        String body = "<html>"
                + "<body>"
                + "<p style='font-weight: bold;'>Kính gửi " + user.getFullName() + ",</p>"
                + "<p>Cảm ơn bạn đã tin tưởng và sử dụng dịch vụ của chúng tôi. Đơn hàng của bạn đã được xác nhận và sẵn sàng để nhận tại showroom của chúng tôi. Vui lòng đến cửa hàng để nhận đơn hàng. Bạn có thể xem chi tiết đơn hàng bên dưới:</p>"
                + "<p><strong>THÔNG TIN ĐƠN HÀNG " + order.getId() + "</strong></p>"
                + "<p><strong>Sản phẩm thuê:</strong></p>"
                + "<p>" + result.toString() + "</p>"
                + "<p><strong>Tổng giá trị: </strong>" + order.getTotalMoney().longValue() + " VND</p>"
                + "<p><strong>Địa chỉ nhận hàng: </strong>" + "268, Lý Thường Kiệt, Phường 14, Quận 10, TP.HCM"
                + "</p>"
                + "<br>"
                + "<p>Để thay đổi bất kỳ thông tin nào, vui lòng liên hệ với chúng tôi ít nhất 24 giờ trước.</p>"
                + "<p>Vì giá trị sản phẩm của bạn khá cao, vui lòng chuẩn bị khoản tiền thế chấp là "
                + collateral.longValue() + " VND khi đến cửa hàng để nhận hàng.</p>"
                + "<p>Trân trọng,<br> Bộ phận Chăm Sóc Khách Hàng Bee RushTech</p>"
                + "<br>"
                + "<p><strong>Lưu ý:</strong></p>"
                + "<ul>"
                + "<li style='color: red;'>Trước khi nhận hàng, vui lòng kiểm tra để đảm bảo gói hàng không bị hư hỏng hoặc bị can thiệp. Nếu gói hàng bị hư hỏng và bạn nghi ngờ rằng nó đã bị mở trước, hãy tạo phiếu khiếu nại với đơn vị vận chuyển. Chỉ bằng cách này, chúng tôi mới có thể xác định bên chịu trách nhiệm và nếu cần, hoàn tiền hoặc sắp xếp lô hàng khác.</li>"
                + "<br>"
                + "<li style='color: red;'>Trong thời gian thuê, nếu thiết bị bị hư hại, người thuê chịu trách nhiệm toàn bộ chi phí sửa chữa. Nếu thiết bị không thể sửa chữa, người thuê phải bồi thường giá trị mua mới của thiết bị. Ngoài ra, người thuê phải bồi thường cho bất kỳ thiệt hại kinh tế nào do thiết bị không thể cho thuê.</li>"
                + "</ul>"
                + "<br>"
                + "<p>Vui lòng liên hệ với chúng tôi qua các cách sau:</p>"
                + "<p>Email: hien.nguyenhophuoc@hcmut.edu.vn</p>"
                + "<p>Điện thoại: 0869018053</p>"
                + "<p>Showroom: 268, Lý Thường Kiệt, Phường 14, Quận 10, TP.HCM.</p>"
                + "</body>"
                + "</html>";

        Email email = new Email();
        email.setToEmail(toEmail);
        email.setSubject(subject);
        email.setBody(body);
        sendEmail(email);
    }

    public void handleSendCancelOrder(Order order) throws Exception {
        Long userId = order.getUser().getId();
        User user = userService.findById(userId);
        String toEmail = user.getEmail();
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail detail = orderDetails.get(i);

            Long days = detail.getTimeRenting() / 24;
            Long hours = detail.getTimeRenting() % 24;

            // Định dạng chuỗi kết quả
            String timeRenting = String.format("%d Days, %d hours", days, hours);

            result.append(String.format("<p> %d. Product: %s - Quantity: %d - Time Renting: %s </p>",
                    i + 1,
                    detail.getProduct().getName(),
                    detail.getNumberOfProducts(),
                    timeRenting));
        }

        String subject = "[BeeRushTech] Xác Nhận Hủy Đơn Hàng";
        String body = "<html>"
                + "<body>"
                + "<p style='font-weight: bold;'>Kính gửi " + user.getFullName() + ",</p>"
                + "<p>Chúng tôi đã nhận được yêu cầu hủy đơn hàng của bạn và xin thông báo rằng đơn hàng của bạn đã được hủy thành công. Dưới đây là thông tin chi tiết về đơn hàng đã hủy:</p>"
                + "<p><strong>MÃ ĐƠN HÀNG: </strong>" + order.getId() + "</p>"
                + "<p><strong>Sản phẩm thuê:</strong></p>"
                + "<p>" + result.toString() + "</p>"
                + "<p><strong>Tổng giá trị: </strong>" + order.getTotalMoney().longValue() + " VND</p>"
                + "<p><strong>Phương thức thanh toán: </strong>" + order.getPaymentMethod() + "</p>"
                + "<br>"
                + "<p>Yêu cầu hủy đơn hàng của bạn đã được xử lý thành công. Nếu bạn đã thanh toán, khoản hoàn tiền sẽ được xử lý qua phương thức thanh toán của bạn. Vui lòng chờ vài ngày làm việc để hoàn tất quá trình hoàn tiền.</p>"
                + "<p>Nếu việc hủy đơn hàng liên quan đến sản phẩm thuê, bạn không cần phải trả lại sản phẩm. Nếu gặp bất kỳ vấn đề nào hoặc có thêm câu hỏi, vui lòng liên hệ với chúng tôi.</p>"
                + "<p>Trân trọng,<br> Bộ phận Chăm Sóc Khách Hàng Bee RushTech</p>"
                + "<br>"
                + "<p><strong>Lưu ý:</strong></p>"
                + "<ul>"
                + "<li style='color: red;'>Nếu bạn có bất kỳ thắc mắc nào về quá trình hủy hoặc hoàn tiền, vui lòng liên hệ với chúng tôi càng sớm càng tốt để được giải đáp.</li>"
                + "<br>"
                + "<li style='color: red;'>Nếu bạn gặp vấn đề với quá trình hoàn tiền, vui lòng liên hệ bộ phận chăm sóc khách hàng để được hỗ trợ.</li>"
                + "</ul>"
                + "<br>"
                + "<p>Bạn có thể liên hệ với chúng tôi qua các kênh sau:</p>"
                + "<p>Email: hien.nguyenhophuoc@hcmut.edu.vn</p>"
                + "<p>Điện thoại: 0869018053</p>"
                + "<p>Showroom: 268, Lý Thường Kiệt, Phường 14, Quận 10, TP.HCM.</p>"
                + "</body>"
                + "</html>";

        Email email = new Email();
        email.setToEmail(toEmail);
        email.setSubject(subject);
        email.setBody(body);
        sendEmail(email);
    }

    public void handleSendMailReturn(Order order, String methodReturn) throws Exception {
        Long userId = order.getUser().getId();
        User user = userService.findById(userId);
        String toEmail = user.getEmail();
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail detail = orderDetails.get(i);

            Long days = detail.getTimeRenting() / 24;
            Long hours = detail.getTimeRenting() % 24;

            // Định dạng chuỗi kết quả
            String timeRenting = String.format("%d Days, %d hours", days, hours);

            result.append(String.format("<p> %d. Product: %s - Quantity: %d - Time Renting: %s </p>",
                    i + 1,
                    detail.getProduct().getName(),
                    detail.getNumberOfProducts(),
                    timeRenting));
        }

        String subject = "[BeeRushTech] Xác nhận yêu cầu trả sản phẩm";

        String body = "";
        if (methodReturn.equals("store")) {
            body = "<html>"
                    + "<body>"
                    + "<p style='font-weight: bold;'>Kính gửi " + user.getFullName() + ",</p>"
                    + "<p>Chúng tôi đã nhận được yêu cầu trả lại sản phẩm thuê của bạn. Chúng tôi xin xác nhận rằng yêu cầu trả sản phẩm của bạn đang được xử lý:</p>"
                    + "<p><strong>MÃ ĐƠN HÀNG: </strong>" + order.getId() + "</p>"
                    + "<p><strong>Sản phẩm thuê:</strong></p>"
                    + "<p>" + result.toString() + "</p>"
                    + "<br>"
                    + "<p>Để hoàn tất quá trình trả sản phẩm, vui lòng mang sản phẩm đến showroom của chúng tôi trước ngày hết hạn. Xin lưu ý rằng việc trả sản phẩm sau ngày hết hạn có thể dẫn đến các khoản phí phát sinh theo chính sách thuê của chúng tôi.</p>"
                    + "<br>"
                    + "<p><strong>Địa chỉ trả sản phẩm: 268, Lý Thường Kiệt, Phường 14, Quận 10, TP.HCM</strong></p>"
                    + "<br>"
                    + "<p>Nếu bạn cần hỗ trợ hoặc có bất kỳ câu hỏi nào về quá trình trả sản phẩm, vui lòng liên hệ với chúng tôi.</p>"
                    + "<br>"
                    + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi. Chúng tôi hy vọng được tiếp tục phục vụ bạn trong tương lai!</p>"
                    + "<p>Trân trọng,<br> Bộ phận Chăm Sóc Khách Hàng Bee RushTech</p>"
                    + "<br>"
                    + "<p><strong>Lưu ý quan trọng:</strong></p>"
                    + "<ul>"
                    + "<li style='color: red;'>Vui lòng đảm bảo rằng sản phẩm được trả lại trong tình trạng như khi thuê. Sản phẩm bị hư hỏng hoặc thiếu có thể dẫn đến các khoản phí phát sinh.</li>"
                    + "<br>"
                    + "<li style='color: red;'>Nếu bạn không thể trả sản phẩm trước ngày hết hạn, vui lòng liên hệ với chúng tôi ngay lập tức để sắp xếp gia hạn hoặc giải pháp thay thế.</li>"
                    + "</ul>"
                    + "<br>"
                    + "<p>Bạn có thể liên hệ với chúng tôi qua các kênh sau:</p>"
                    + "<p>Email: hien.nguyenhophuoc@hcmut.edu.vn</p>"
                    + "<p>Điện thoại: 0869018053</p>"
                    + "<p>Showroom: 268, Lý Thường Kiệt, Phường 14, Quận 10, TP.HCM</p>"
                    + "</body>"
                    + "</html>";

        } else if (methodReturn.equals("home")) {
            body = "<html>"
                    + "<body>"
                    + "<p style='font-weight: bold;'>Kính gửi " + user.getFullName() + ",</p>"
                    + "<p>Chúng tôi đã nhận được yêu cầu trả lại sản phẩm thuê của bạn. Chúng tôi xin xác nhận rằng yêu cầu trả sản phẩm của bạn đang được xử lý:</p>"
                    + "<p><strong>MÃ ĐƠN HÀNG: </strong>" + order.getId() + "</p>"
                    + "<p><strong>Sản phẩm thuê:</strong></p>"
                    + "<p>" + result.toString() + "</p>"
                    + "<br>"
                    + "<p>Để hoàn tất quá trình trả sản phẩm, đội ngũ của chúng tôi sẽ đến địa chỉ mà bạn đã cung cấp để nhận lại sản phẩm. Xin lưu ý rằng việc trả sản phẩm chậm trễ có thể dẫn đến các khoản phí phát sinh theo chính sách thuê của chúng tôi.</p>"
                    + "<br>"
                    + "<p><strong>Địa chỉ trả sản phẩm (Địa chỉ của bạn):</strong> " + user.getAddress() + "</p>"
                    + "<br>"
                    + "<p>Nếu bạn cần hỗ trợ hoặc có bất kỳ câu hỏi nào về quá trình trả sản phẩm, vui lòng liên hệ với chúng tôi.</p>"
                    + "<br>"
                    + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi. Chúng tôi hy vọng được tiếp tục phục vụ bạn trong tương lai!</p>"
                    + "<p>Trân trọng,<br> Bộ phận Chăm Sóc Khách Hàng Bee RushTech</p>"
                    + "<br>"
                    + "<p><strong>Lưu ý quan trọng:</strong></p>"
                    + "<ul>"
                    + "<li style='color: red;'>Vui lòng đảm bảo rằng sản phẩm được trả lại trong tình trạng như khi thuê. Sản phẩm bị hư hỏng hoặc thiếu có thể dẫn đến các khoản phí phát sinh.</li>"
                    + "<br>"
                    + "<li style='color: red;'>Nếu bạn không thể trả sản phẩm đúng thời hạn, vui lòng liên hệ với chúng tôi ngay lập tức để sắp xếp gia hạn hoặc giải pháp thay thế.</li>"
                    + "</ul>"
                    + "<br>"
                    + "<p>Bạn có thể liên hệ với chúng tôi qua các kênh sau:</p>"
                    + "<p>Email: hien.nguyenhophuoc@hcmut.edu.vn</p>"
                    + "<p>Điện thoại: 0869018053</p>"
                    + "<p>Showroom: 268, Lý Thường Kiệt, Phường 14, Quận 10, TP.HCM</p>"
                    + "</body>"
                    + "</html>";

        }

        Email email = new Email();
        email.setToEmail(toEmail);
        email.setSubject(subject);
        email.setBody(body);
        sendEmail(email);
    }
}
