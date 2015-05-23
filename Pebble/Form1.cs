using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Net.WebSockets;
using System.Security.Permissions;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Pebble
{
    [PermissionSet(SecurityAction.Demand, Name = "FullTrust")]
    [System.Runtime.InteropServices.ComVisibleAttribute(true)]
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();

            webBrowser.ObjectForScripting = this;
        }

        public void Test(String message)
        {
            MessageBox.Show(message, "client code");
        }

        private async void button1_Click(object sender, EventArgs e)
        {
            ClientWebSocket webSocket = null;

            try
            {
                webSocket = new ClientWebSocket();
                await webSocket.ConnectAsync(new Uri("ws://192.168.43.98:6666"), CancellationToken.None);
                await Task.WhenAll(Receive(webSocket), Send(webSocket));
            }
            catch (Exception ex)
            {
                MessageBox.Show("Exception: " + ex);
            }
            finally
            {
                if (webSocket != null)
                    webSocket.Dispose();

                MessageBox.Show("WebSocket closed.");
            }
        }


        private static readonly TimeSpan delay = TimeSpan.FromMilliseconds(30000);
        private const int sendChunkSize = 256;
        private const int receiveChunkSize = 256;
        private const bool verbose = true;
        static UTF8Encoding encoder = new UTF8Encoding();

        private static async Task Send(ClientWebSocket webSocket)
        {

            //byte[] buffer = encoder.GetBytes("{\"op\":\"blocks_sub\"}"); //"{\"op\":\"unconfirmed_sub\"}");
            byte[] buffer = encoder.GetBytes("pizdeczka");
            await webSocket.SendAsync(new ArraySegment<byte>(buffer), WebSocketMessageType.Text, true, CancellationToken.None);

            while (webSocket.State == WebSocketState.Open)
            {
                LogStatus(false, buffer, buffer.Length);
                await Task.Delay(delay);
            }
        }

        private static async Task Receive(ClientWebSocket webSocket)
        {
            byte[] buffer = new byte[receiveChunkSize];
            while (webSocket.State == WebSocketState.Open)
            {
                var result = await webSocket.ReceiveAsync(new ArraySegment<byte>(buffer), CancellationToken.None);
                if (result.MessageType == WebSocketMessageType.Close)
                {
                    await webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, string.Empty, CancellationToken.None);
                }
                else
                {
                    LogStatus(true, buffer, result.Count);
                }
            }
        }

        private static void LogStatus(bool receiving, byte[] buffer, int length)
        {
            if (verbose)
                MessageBox.Show(encoder.GetString(buffer));
        }

    }
}
