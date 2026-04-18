<?php
header("Content-Type: application/json");
require_once __DIR__ . '/../repository/PhoneBookRepository.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["success" => false, "message" => "Method not allowed"]);
    exit;
}

$body = json_decode(file_get_contents("php://input"), true);

if (empty($body['full_name']) || empty($body['phone_number'])) {
    echo json_encode(["success" => false, "message" => "Missing required fields"]);
    exit;
}

$repo   = new PhoneBookRepository();
$result = $repo->addEntry($body['full_name'], $body['phone_number'], "android");

switch ($result) {
    case "inserted":
        echo json_encode(["success" => true,  "message" => "Entry saved successfully"]);
        break;
    case "duplicate":
        echo json_encode(["success" => true,  "message" => "Already exists"]);
        break;
    default:
        echo json_encode(["success" => false, "message" => "Insert failed"]);
}
?>