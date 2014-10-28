package com.mendeley.api.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * SSLSocketFactory that wraps one existing SSLSocketFactory and delegetes into it adding
 * a new cipher suite
 */
public class TSLOnlySocketFactory extends SSLSocketFactory {

    private final SSLSocketFactory delegate;

    public TSLOnlySocketFactory(SSLSocketFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public String[] getDefaultCipherSuites() {

        return getPreferredDefaultCipherSuites(this.delegate);
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return getPreferredSupportedCipherSuites(this.delegate);
    }



    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        final Socket socket = this.delegate.createSocket(s, host, port, autoClose);

        ((SSLSocket)socket).setEnabledCipherSuites(getPreferredDefaultCipherSuites(delegate));
        ((SSLSocket)socket).setEnabledProtocols(getEnabledProtocols((SSLSocket)socket));

        return socket;
    }



    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        final Socket socket = this.delegate.createSocket(host, port);

        ((SSLSocket)socket).setEnabledCipherSuites(getPreferredDefaultCipherSuites(delegate));
        ((SSLSocket) socket).setEnabledProtocols(getEnabledProtocols((SSLSocket)socket));

        return socket;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        final Socket socket = this.delegate.createSocket(host, port, localHost, localPort);

        ((SSLSocket)socket).setEnabledCipherSuites(getPreferredDefaultCipherSuites(delegate));
        ((SSLSocket) socket).setEnabledProtocols(getEnabledProtocols((SSLSocket)socket));

        return socket;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        final Socket socket = this.delegate.createSocket(host, port);

        ((SSLSocket)socket).setEnabledCipherSuites(getPreferredDefaultCipherSuites(delegate));
        ((SSLSocket) socket).setEnabledProtocols(getEnabledProtocols((SSLSocket)socket));

        return socket;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        final Socket socket = this.delegate.createSocket(address, port, localAddress, localPort);

        ((SSLSocket)socket).setEnabledCipherSuites(getPreferredDefaultCipherSuites(delegate));
        ((SSLSocket) socket).setEnabledProtocols(getEnabledProtocols((SSLSocket)socket));

        return socket;
    }

    private String[] getPreferredDefaultCipherSuites(SSLSocketFactory sslSocketFactory) {
        return getCipherSuites(sslSocketFactory.getDefaultCipherSuites());
    }

    private String[] getPreferredSupportedCipherSuites(SSLSocketFactory sslSocketFactory) {
        return getCipherSuites(sslSocketFactory.getSupportedCipherSuites());
    }

    private String[] getCipherSuites(String[] cipherSuites) {
        final ArrayList<String> suitesList = new ArrayList<String>(Arrays.asList(cipherSuites));
        final Iterator<String> iterator = suitesList.iterator();
        while (iterator.hasNext()) {
            final String cipherSuite = iterator.next();
            if (!cipherSuite.contains("SSL")) {
                iterator.remove();
            }
        }
        return suitesList.toArray(new String[suitesList.size()]);
    }

    private String[] getEnabledProtocols(SSLSocket socket) {
        final ArrayList<String> protocolList = new ArrayList<String>(Arrays.asList(socket.getSupportedProtocols()));
        final Iterator<String> iterator = protocolList.iterator();
        while (iterator.hasNext()) {
            final String protocl = iterator.next();
            if (protocl.contains("SSL")) {
                iterator.remove();
            }
        }
        return protocolList.toArray(new String[protocolList.size()]);
     }


}
