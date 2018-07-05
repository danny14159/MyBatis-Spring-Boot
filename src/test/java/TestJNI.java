import com.sun.glass.events.KeyEvent;
import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.*;

import java.util.List;

public class TestJNI {

    public static void main(String[] args) throws Exception {
        /*int hwnd = User32.INSTANCE.FindWindowA(null, null);
        User
        System.setProperty("jna.encoding", "utf8");//设置编码，防止乱码
        User32.INSTANCE.MessageBoxA(hwnd, "看我闪瞎你的狗眼", 0, 0);//调用消息对话框
        int i = 0;
        while (true) {
            User32.INSTANCE.BlockInput(true);//阻塞鼠标键盘的输入
            User32.INSTANCE.SendMessageA(hwnd, 0x0112, 0xF170, 2);//关闭显示器
            Thread.sleep(2000);//间隔2秒
            User32.INSTANCE.SendMessageA(hwnd, 0x0112, 0xF170, -1);//打开显示器
            Thread.sleep(2000);//间隔2秒
            i++;
            if (i > 10) {
                break;
            }
        }
        handle = win32process.CreateProcess("C:\Program Files (x86)\Tencent\QQPlayer\QQPlayer.exe",
                                        "C:\Program Files (x86)\Tencent\QQPlayer\QQPlayer.exe", None, None, 0,
                                        win32process.CREATE_NO_WINDOW,
                                        None, None,
                                        win32process.STARTUPINFO())
        User32.INSTANCE.BlockInput(false);//释放鼠标键盘*/
        //Kernel32.INSTANCE.CreateProcess("C:\\Program Files (x86)\\Tencent\\QQPlayer\\QQPlayer.exe","",null,null,false,)
        WinBase.STARTUPINFO startupInfo = new WinBase.STARTUPINFO();
        WinBase.PROCESS_INFORMATION.ByReference processInformation = new WinBase.PROCESS_INFORMATION.ByReference();

        boolean status = Kernel32.INSTANCE.CreateProcess(
                "C:\\Program Files (x86)\\Tencent\\QQPlayer\\QQPlayer.exe",
                "",
                null,
                null,
                true,
                new WinDef.DWORD(0),
                Pointer.NULL,
                System.getProperty("java.io.tmpdir"),
                startupInfo,
                processInformation);
        Thread.sleep(1000);
        DesktopWindow desktopWindow = getWindowByProcessPath("QQPlayer.exe".toLowerCase());

        User32.INSTANCE.PostMessage(desktopWindow.getHWND(),  WinUser.WM_KEYDOWN, new WinDef.WPARAM(KeyEvent.VK_SPACE), new WinDef.LPARAM(0));
        User32.INSTANCE.PostMessage(desktopWindow.getHWND(),  WinUser.WM_KEYUP  , new WinDef.WPARAM(KeyEvent.VK_SPACE), new WinDef.LPARAM(0));
        Kernel32.INSTANCE.WaitForSingleObject(processInformation.hProcess, -1);
    }

    private static DesktopWindow getWindowByProcessPath(final String filePathEnd) {
        final List<DesktopWindow> allWindows = WindowUtils.getAllWindows(true);
        for (final DesktopWindow wnd : allWindows) {
            System.out.println(wnd.getFilePath());
            if (wnd.getFilePath().toLowerCase()
                    .endsWith(filePathEnd.toLowerCase())) {

                return wnd;
            }
        }

        return null;
    }
}
