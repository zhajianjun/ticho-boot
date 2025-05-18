package top.ticho.intranet.server.repository;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.entity.ClientInfo;

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
public class ClientRepository {
    /**
     * 客户端与服务端的通道
     */
    private final Map<String, ClientInfo> clientMap = new ConcurrentHashMap<>();

    public Optional<ClientInfo> findByAccessKey(String accessKey) {
        return Optional.ofNullable(clientMap.get(accessKey));
    }

    public List<ClientInfo> findAll() {
        return clientMap.values().stream().toList();
    }

    public Optional<ClientInfo> findByPort(Integer port) {
        return clientMap.values()
            .stream()
            .filter(Objects::nonNull)
            .filter(x -> x.getPortMap().containsKey(port))
            .findFirst();
    }

    public boolean saveClient(String accessKey, String name) {
        if (StrUtil.isBlank(accessKey)) {
            return false;
        }
        if (clientMap.containsKey(accessKey)) {
            return false;
        }
        clientMap.put(accessKey, new ClientInfo(accessKey, name));
        return true;
    }

    /**
     * 根据accessKey删除客户端
     */
    public boolean deleteClient(String accessKey) {
        if (StrUtil.isBlank(accessKey)) {
            return false;
        }
        ClientInfo clientInfoGet = clientMap.get(accessKey);
        if (Objects.isNull(clientInfoGet)) {
            return false;
        }
        clientMap.remove(accessKey);
        return true;
    }

    public boolean deleteAllClient() {
        if (MapUtil.isEmpty(clientMap)) {
            return false;
        }
        Set<String> accessKeys = clientMap.keySet();
        accessKeys.forEach(this::deleteClient);
        return true;
    }

    public Channel getRequestChannel(Channel channel, String requestId) {
        if (null == channel || StrUtil.isBlank(requestId)) {
            return null;
        }
        Map<String, Channel> requestChannelMap = channel.attr(CommConst.REQUEST_ID_ATTR_MAP).get();
        if (MapUtil.isEmpty(requestChannelMap) && !requestChannelMap.containsKey(requestId)) {
            return null;
        }
        return requestChannelMap.get(requestId);
    }

    public Channel removeRequestChannel(String accessKey, String requestId) {
        Optional<ClientInfo> clientInfoOpt = findByAccessKey(accessKey);
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
        if (MapUtil.isNotEmpty(requestChannelMap) && requestChannelMap.containsKey(requestId)) {
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

    public void closeRequestChannel(ClientInfo clientInfo) {
        if (Objects.isNull(clientInfo)) {
            return;
        }
        Channel channel = clientInfo.getChannel();
        if (Objects.isNull(channel)) {
            return;
        }
        Map<String, Channel> requestChannelMap = channel.attr(CommConst.REQUEST_ID_ATTR_MAP).get();
        if (MapUtil.isNotEmpty(requestChannelMap)) {
            requestChannelMap.values().forEach(IntranetUtil::close);
            requestChannelMap.clear();
        }
        IntranetUtil.close(channel);
        clientInfo.disconnect();
    }

}
