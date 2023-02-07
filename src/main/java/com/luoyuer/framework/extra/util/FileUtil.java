package com.luoyuer.framework.extra.util;

import com.luoyuer.framework.Holder;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Audio;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

public class FileUtil {
    public static Image uploadImage(byte[] bytes) {
        Integer messageType = Holder.messageType.get();
        if (messageType == null) {
            return null;
        }

        if (messageType == 1) {
            Contact friend = Holder.friend.get();
            try (ExternalResource externalResource = ExternalResource.create(bytes)) {
                return friend.uploadImage(externalResource);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Contact group = Holder.group.get();
            try (ExternalResource externalResource = ExternalResource.create(bytes)) {
                return group.uploadImage(externalResource);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Audio uploadAudio(byte[] bytes) {
        Integer messageType = Holder.messageType.get();
        if (messageType == null) {
            return null;
        }
        if (messageType == 1) {
            Friend friend = Holder.friend.get();
            try (ExternalResource externalResource = ExternalResource.create(bytes)) {
                return friend.uploadAudio(externalResource);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Group group = Holder.group.get();
            try (ExternalResource externalResource = ExternalResource.create(bytes)) {
                return group.uploadAudio(externalResource);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
