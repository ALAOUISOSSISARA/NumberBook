<?php
class DbConnector {
    private $host     = "localhost";
    private $database = "numberbook";
    private $user     = "root";
    private $pass     = "";
    private $pdo;

    public function open() {
        $this->pdo = null;
        try {
            $dsn = "mysql:host={$this->host};dbname={$this->database};charset=utf8mb4";
            $this->pdo = new PDO($dsn, $this->user, $this->pass);
            $this->pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch (PDOException $e) {
            echo json_encode(["error" => $e->getMessage()]);
            exit;
        }
        return $this->pdo;
    }
}
?>