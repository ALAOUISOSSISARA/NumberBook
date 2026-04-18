<?php
require_once __DIR__ . '/../config/DbConnector.php';

class PhoneBookRepository {
    private $pdo;
    private $tableName = "phonebook_entry";

    public function __construct() {
        $connector = new DbConnector();
        $this->pdo = $connector->open();
    }

    public function addEntry($fullName, $phoneNumber, $source = "android") {
        // Block exact duplicate (same name + same number)
        if ($this->entryExists($fullName, $phoneNumber)) {
            return "duplicate";
        }

        $sql = "INSERT INTO {$this->tableName} (full_name, phone_number, entry_source)
                VALUES (:full_name, :phone_number, :entry_source)";
        $stmt = $this->pdo->prepare($sql);
        $result = $stmt->execute([
            ':full_name'    => $fullName,
            ':phone_number' => $phoneNumber,
            ':entry_source' => $source
        ]);
        return $result ? "inserted" : "failed";
    }

    private function entryExists($fullName, $phoneNumber) {
        $sql  = "SELECT COUNT(*) FROM {$this->tableName}
                 WHERE full_name = :name AND phone_number = :phone";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([
            ':name'  => $fullName,
            ':phone' => $phoneNumber
        ]);
        return $stmt->fetchColumn() > 0;
    }

    public function fetchAll() {
        $sql  = "SELECT * FROM {$this->tableName} ORDER BY full_name ASC";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function findByKeyword($keyword) {
        $sql = "SELECT * FROM {$this->tableName}
                WHERE full_name LIKE :kw OR phone_number LIKE :kw
                ORDER BY full_name ASC";
        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([':kw' => '%' . $keyword . '%']);
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }
}
?>