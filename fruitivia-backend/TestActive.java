import com.fruitivia.user.User;
public class TestActive {
    public static void main(String[] args) {
        User u = User.builder().build();
        System.out.println(u.isActive());
    }
}
