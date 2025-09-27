package top.ticho.intranet.server.support;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.entity.IntranetClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhajianjun
 * @date 2025-05-18 11:44
 */
@Slf4j
public class IntranetClientSupport {
    /**
     * 客户端与服务端的通道
     */
    private final Map<String, IntranetClient> clientMap = new ConcurrentHashMap<>();

    public Optional<IntranetClient> findByAccessKey(String accessKey) {
        if (Objects.isNull(accessKey)) {
            return Optional.empty();
        }
        return Optional.ofNullable(clientMap.get(accessKey));
    }

    public List<IntranetClient> findAll() {
        return new ArrayList<>(clientMap.values());
    }

    public Optional<IntranetClient> findByPort(Integer port) {
        return clientMap.values()
            .stream()
            .filter(Objects::nonNull)
            .filter(x -> x.getPortMap().containsKey(port))
            .findFirst();
    }

    public boolean create(String accessKey, String name) {
        if (Objects.isNull(accessKey)) {
            return false;
        }
        if (clientMap.containsKey(accessKey)) {
            log.warn("创建客户端失败，密钥：{}已存在", accessKey);
            return false;
        }
        clientMap.put(accessKey, new IntranetClient(accessKey, name));
        log.info("创建客户端成功，密钥：{}", accessKey);
        return true;
    }

    /**
     * 根据accessKey删除客户端
     */
    public boolean deleteClient(String accessKey) {
        if (Objects.isNull(accessKey)) {
            return false;
        }
        IntranetClient intranetClientGet = clientMap.get(accessKey);
        if (Objects.isNull(intranetClientGet)) {
            log.warn("移除客户端失败，密钥：{}不存在", accessKey);
            return false;
        }
        clientMap.remove(accessKey);
        log.info("移除客户端成功，密钥：{}", accessKey);
        return true;
    }

    public boolean deleteAllClient() {
        if (clientMap.isEmpty()) {
            return false;
        }
        Set<String> accessKeys = clientMap.keySet();
        accessKeys.forEach(this::deleteClient);
        return true;
    }

    public Channel getRequestChannel(Channel channel, String requestId) {
        if (null == channel || Objects.isNull(requestId)) {
            return null;
        }
        Map<String, Channel> requestChannelMap = channel.attr(CommConst.REQUEST_ID_ATTR_MAP).get();
        if (Objects.nonNull(requestChannelMap) && !requestChannelMap.isEmpty() && !requestChannelMap.containsKey(requestId)) {
            return null;
        }
        return requestChannelMap.get(requestId);
    }

    public Channel removeRequestChannel(String accessKey, String requestId) {
        Optional<IntranetClient> clientInfoOpt = findByAccessKey(accessKey);
        return clientInfoOpt
            .map(item -> removeRequestChannel(item.getChannel(), requestId))
            .orElse(null);
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public Channel removeRequestChannel(Channel channel, String requestId) {
        if (null == channel) {
            return null;
        }
        Map<String, Channel> requestChannelMap = channel.attr(CommConst.REQUEST_ID_ATTR_MAP).get();
        if (Objects.nonNull(requestChannelMap) && requestChannelMap.containsKey(requestId)) {
            synchronized (channel) {
                return requestChannelMap.remove(requestId);
            }
        }
        return null;
    }

    public void closeRequestChannelByAccessKey(String accessKey) {
        findByAccessKey(accessKey).ifPresent(this::closeRequestChannel);
    }

    public void closeRequestChannelByChannel(Channel channel) {
        clientMap.values()
            .stream()
            .filter(x -> Objects.equals(channel, x.getChannel()))
            .findFirst()
            .ifPresent(this::closeRequestChannel);
    }

    public void closeRequestChannel(IntranetClient intranetClient) {
        if (Objects.isNull(intranetClient)) {
            return;
        }
        Channel channel = intranetClient.getChannel();
        if (Objects.isNull(channel)) {
            return;
        }
        Map<String, Channel> requestChannelMap = channel.attr(CommConst.REQUEST_ID_ATTR_MAP).get();
        if (Objects.nonNull(requestChannelMap)) {
            requestChannelMap.values().forEach(IntranetUtil::close);
            requestChannelMap.clear();
        }
        IntranetUtil.close(channel);
        intranetClient.disconnect();
    }

}
