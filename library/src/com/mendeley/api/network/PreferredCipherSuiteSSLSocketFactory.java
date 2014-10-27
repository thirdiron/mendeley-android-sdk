package com.mendeley.api.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * SSLSocketFactory that wraps one existing SSLSocketFactory and delegetes into it adding
 * a new cipher suite
 */
public class PreferredCipherSuiteSSLSocketFactory extends SSLSocketFactory {

    private final String preferedCipherSuite;

    private final SSLSocketFactory delegate;

    public PreferredCipherSuiteSSLSocketFactory(SSLSocketFactory delegate, String preferedCipherSuite) {
        this.delegate = delegate;
        this.preferedCipherSuite = preferedCipherSuite;
    }

    @Override
    public String[] getDefaultCipherSuites() {

        return setupPreferredDefaultCipherSuites(this.delegate);
    }

    @Override
    public String[] getSupportedCipherSuites() {

        return setupPreferredSupportedCipherSuites(this.delegate);
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        final Socket socket = this.delegate.createSocket(s, host, port, autoClose);
        ((SSLSocket)socket).setEnabledCipherSuites(setupPreferredDefaultCipherSuites(delegate));

        return socket;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        final Socket socket = this.delegate.createSocket(host, port);
        ((SSLSocket)socket).setEnabledCipherSuites(setupPreferredDefaultCipherSuites(delegate));

        return socket;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        final Socket socket = this.delegate.createSocket(host, port, localHost, localPort);
        ((SSLSocket)socket).setEnabledCipherSuites(setupPreferredDefaultCipherSuites(delegate));

        return socket;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        final Socket socket = this.delegate.createSocket(host, port);
        ((SSLSocket)socket).setEnabledCipherSuites(setupPreferredDefaultCipherSuites(delegate));

        return socket;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        final Socket socket = this.delegate.createSocket(address, port, localAddress, localPort);
        ((SSLSocket)socket).setEnabledCipherSuites(setupPreferredDefaultCipherSuites(delegate));

        return socket;
    }

    private String[] setupPreferredDefaultCipherSuites(SSLSocketFactory sslSocketFactory) {
        return setupCipherSuites(sslSocketFactory.getDefaultCipherSuites());
    }

    private String[] setupPreferredSupportedCipherSuites(SSLSocketFactory sslSocketFactory) {
        return setupCipherSuites(sslSocketFactory.getSupportedCipherSuites());
    }

    private String[] setupCipherSuites(String[] cipherSuites) {
        final ArrayList<String> suitesList = new ArrayList<String>(Arrays.asList(cipherSuites));
        suitesList.remove(preferedCipherSuite);
        suitesList.add(0, preferedCipherSuite);

        return suitesList.toArray(new String[suitesList.size()]);
    }
}
