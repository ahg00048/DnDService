package es.ujaen.ahg00048.microservice_characterSheet.service;

import es.ujaen.ahg00048.microservice_characterSheet.exception.InvalidOperationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import es.ujaen.ahg00048.microservice_characterSheet.repository.CharacterSheetRepository;
import es.ujaen.ahg00048.microservice_characterSheet.entity.characterSheet.CharacterSheet;
import es.ujaen.ahg00048.microservice_characterSheet.exception.CharacterSheetRegistrationException;

@Service
@Validated
public class CharacterSheetService {
    @Autowired
    private Environment _env;

    @Autowired
    private CharacterSheetRepository _charSheetsRep;

    @Autowired
    private RedisLockRegistry _lockRegistry;

    private final static String LOCK_USER_KEY_BASE = "charSheets:";
    private final static int TIMEOUT_AMOUNT = 1;
    private final static TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;


    public static int MAX_NUMBER_SHEETS_PER_USER;


    @Autowired
    public CharacterSheetService(@Value("${app.user.max.characterSheets}") int max_number_sheets_per_user) {
        MAX_NUMBER_SHEETS_PER_USER = max_number_sheets_per_user;
    }

    @Transactional
    public List<CharacterSheet> getCharSheets(@Email @NotBlank String userId) {
        return _charSheetsRep.findAllByUserId(userId);
    }


    public CharacterSheet addCharSheet(@Email @NotBlank String userId, @Valid CharacterSheet charSheet)
            throws InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_USER_KEY_BASE + userId);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();
            locked = true;

            charSheet.setUserId(userId);

            if (_charSheetsRep.countAllByUserId(charSheet.getUserId()) >= MAX_NUMBER_SHEETS_PER_USER)
                throw new InvalidOperationException();

            return _charSheetsRep.insert(charSheet);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    public CharacterSheet modifyCharSheet(@Email @NotBlank String userId, @NotBlank String id, @Valid CharacterSheet charSheet)
            throws CharacterSheetRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_USER_KEY_BASE + userId);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();
            locked = true;
            CharacterSheet savedCharSheet = _charSheetsRep.findById(id).orElseThrow(CharacterSheetRegistrationException::new);

            if (!savedCharSheet.getUserId().equals(userId))
                throw new InvalidOperationException();

            charSheet.setUserId(userId);
            charSheet.setId(id);

            return _charSheetsRep.save(charSheet);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    public void removeCharSheet(@Email @NotBlank String userId, @NotBlank String id)
            throws CharacterSheetRegistrationException, InvalidOperationException,
            IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_USER_KEY_BASE + userId);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();
            locked = true;

            CharacterSheet savedCharSheet = _charSheetsRep.findById(id).orElseThrow(CharacterSheetRegistrationException::new);

            if (!savedCharSheet.getUserId().equals(userId))
                throw new InvalidOperationException();

            _charSheetsRep.deleteById(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }

    /**
     * Only used by RabbitMQ listener
     */
    public void removeImage_admin(@NotBlank String imageId)
            throws IllegalStateException {
        List<Lock> locks = new ArrayList<>();
        List<CharacterSheet> charSheets = _charSheetsRep.findAllByImageId(imageId);
        Set<String> userIds = new HashSet<>();

        for (CharacterSheet charSheet : charSheets) {
            String userId = charSheet.getUserId();
            if (!userIds.contains(userId)) {
                userIds.add(userId);
                locks.add(_lockRegistry.obtain(LOCK_USER_KEY_BASE + userId));
            }
        }
        int locked = 0;

        try {
            for (Lock lock : locks) {
                if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                    throw new IllegalStateException();

                locked++;
            }

            charSheets = _charSheetsRep.findAllByImageId(imageId);

            for (CharacterSheet charSheet : charSheets) {
                charSheet.setImageId("");
            }

            _charSheetsRep.saveAll(charSheets);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            for (int i = 0; i < locked; i++) {
                locks.get(i).unlock();
            }
        }
    }

    /**
     * Only used by RabbitMQ listener
     */
    public void removeCharSheet_admin(@NotBlank String userId)
            throws IllegalStateException {
        Lock lock = _lockRegistry.obtain(LOCK_USER_KEY_BASE + userId);
        boolean locked = false;
        try {
            if (!lock.tryLock(TIMEOUT_AMOUNT, TIMEOUT_UNIT))
                throw new IllegalStateException();
            locked = true;

            _charSheetsRep.deleteAllByUserId(userId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (locked)
                lock.unlock();
        }
    }
}
